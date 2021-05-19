package com.sinqia.sqspii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.sinqia.sqspii.config.JwkAuthorizationServerConfiguration;
import com.sinqia.sqspii.context.TenantContext;
import com.sinqia.sqspii.domain.StaticQrCodeData;
import com.sinqia.sqspii.entity.DigitalCertificate;
import com.sinqia.sqspii.entity.DynamicQrCode;
import com.sinqia.sqspii.exception.InvalidQrCodeStringToDecodeException;
import com.sinqia.sqspii.exception.UnableToDecodeQrCodeException;
import com.sinqia.sqspii.factory.DynamicQRCodeBuilderFactory;
import com.sinqia.sqspii.factory.StaticQRCodeBuilderFactory;
import com.sinqia.sqspii.repository.DigitalCertificateRepository;
import com.sinqia.sqspii.request.DecodeQrCodeRequest;
import com.sinqia.sqspii.response.DecodeQrCodeResponse;
import com.sinqia.sqspii.response.DecodeStaticQrCodeResponse;
import com.sinqia.sqspii.response.SuccessResponse;
import org.apache.commons.codec.binary.Base64;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

@Service
public class DecodeQrCodeService {

    @Value("${pix.well-know.url}")
    public String wellKnowUrl;

    @Autowired
    private JWKSet jwkSet;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DynamicQRCodeBuilderFactory dynamicQRCodeBuilderFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DigitalCertificateRepository digitalCertificateRepository;

    public DecodeQrCodeResponse decode(DecodeQrCodeRequest message) throws IOException, ParseException, JOSEException, URISyntaxException {

        if (StringUtils.isEmpty(message.getQrCodeString()))
            throw new InvalidQrCodeStringToDecodeException();

        try {
            return decodeStaticQrCode(message.getQrCodeString());
        } catch (NumberFormatException e) {
            return decodeDynamicQrCode(message.getQrCodeString());
        } catch (Exception e) {
            throw new UnableToDecodeQrCodeException();
        }
    }

    private DecodeQrCodeResponse decodeDynamicQrCode(String dynamicQrCodeString) throws IOException, URISyntaxException, ParseException, JOSEException {
        String url = dynamicQRCodeBuilderFactory.getQrCodePayloadUrl(dynamicQrCodeString);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(kp.getKeycloakSecurityContext().getTokenString());

        ResponseEntity<String> response = restTemplate.exchange(RequestEntity.get(new URI(url)).headers(headers).build(), String.class);


        SuccessResponse json = mapper.readValue(response.getBody(), SuccessResponse.class);

        String host = getDomainName(url);
        DigitalCertificate dc = digitalCertificateRepository.findByHost(host).orElseThrow(() -> new RuntimeException("Digital Certificate not found for: " + host));

        return decodeJwsPayload((String) json.getBody(), dc.getId().toString());
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getScheme() + "://" + uri.getAuthority();
    }


    private DecodeQrCodeResponse decodeJwsPayload(String jwsPayload, String kid) throws IOException, ParseException, JOSEException {
        RSAKey rsaKey = jwkSet.getKeyByKeyId(kid).toRSAKey();

        // Load JWK set from URL
        //JWKSet publicKeys = JWKSet.load(new URL(wellKnowUrl));

        JWSVerifier verifier = new RSASSAVerifier(rsaKey.toPublicJWK().toRSAKey());

        // Parse the JWE string
        JWSObject jwsObject = JWSObject.parse(jwsPayload);
        jwsObject.verify(verifier);

        // Extract payload
        DynamicQrCode dynamicQrCode = mapper.readValue(Base64.decodeBase64(jwsObject.getPayload().toBase64URL().decodeToString()), DynamicQrCode.class);

        return dynamicQRCodeBuilderFactory.buildDecodeDynamicQrCodeResponse(dynamicQrCode);
    }

    private DecodeQrCodeResponse decodeStaticQrCode(String message) {
        StaticQrCodeData data = StaticQRCodeBuilderFactory.getInstance().buildDecodeQrCodeResponse(message);

        return DecodeStaticQrCodeResponse.builder()
                .qrCodeType("static")
                .key(data.getKey().getValue())
                .merchantName(data.getMerchantName().getValue())
                .city(data.getMerchantCity().getValue())
                .value(StringUtils.isEmpty(data.getTransactionAmount().getValue()) ? 0 : Double.parseDouble(data.getTransactionAmount().getValue()))
                .transactionIdentifier(StringUtils.isEmpty(data.getTxId().getValue()) ? null : data.getTxId().getValue())
                .additionalInformation(StringUtils.isEmpty(data.getAdditionalInfo().getValue()) ? null : data.getAdditionalInfo().getValue())
                .build();
    }
}
