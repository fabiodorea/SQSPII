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
import com.sinqia.sqspii.entity.DynamicQrCode;
import com.sinqia.sqspii.exception.InvalidQrCodeStringToDecodeException;
import com.sinqia.sqspii.exception.UnableToDecodeQrCodeException;
import com.sinqia.sqspii.factory.DynamicQRCodeBuilderFactory;
import com.sinqia.sqspii.factory.StaticQRCodeBuilderFactory;
import com.sinqia.sqspii.request.DecodeQrCodeRequest;
import com.sinqia.sqspii.response.DecodeQrCodeResponse;
import com.sinqia.sqspii.response.DecodeStaticQrCodeResponse;
import com.sinqia.sqspii.response.SuccessResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    public DecodeQrCodeResponse decode(DecodeQrCodeRequest message) throws IOException, ParseException, JOSEException {

        if (StringUtils.isEmpty(message.getQrCodeString()))
            throw new InvalidQrCodeStringToDecodeException();

        try {
            return decodeStaticQrCode(message.getQrCodeString());
        } catch (NumberFormatException e) {
            String payloadUrl = decodeDynamicQrCode(message.getQrCodeString());
            return decodeJwsPayload(payloadUrl);
        } catch (Exception e){
            throw new UnableToDecodeQrCodeException();
        }
    }

    private String decodeDynamicQrCode(String dynamicQrCodeString) throws JsonProcessingException {
        String url = dynamicQRCodeBuilderFactory.getQrCodePayloadUrl(dynamicQrCodeString);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-TenantID", TenantContext.getCurrentTenant());

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://" + url, HttpMethod.GET, entity, String.class);


        SuccessResponse json = mapper.readValue(response.getBody(), SuccessResponse.class);

        return (String) json.getBody();
    }


    private DecodeQrCodeResponse decodeJwsPayload(String jwsPayload) throws IOException, ParseException, JOSEException {
        RSAKey rsaKey = jwkSet.getKeyByKeyId(JwkAuthorizationServerConfiguration.JWK_KID).toRSAKey();

        // Load JWK set from URL
        JWKSet publicKeys = JWKSet.load(new URL(wellKnowUrl));

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
