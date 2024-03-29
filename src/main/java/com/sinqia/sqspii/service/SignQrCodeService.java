package com.sinqia.sqspii.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.sinqia.sqspii.context.TenantContext;
import com.sinqia.sqspii.entity.Parameter;
import com.sinqia.sqspii.entity.VaultPrivateKey;
import com.sinqia.sqspii.repository.ParameterRepository;
import com.sinqia.sqspii.repository.PrivateKeyRepository;
import com.sinqia.sqspii.response.PrivateKeyResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SignQrCodeService {

    @Value("${pix.well-know.url}")
    private String JWK_URL;

    @Value("${sinqia.vault.host}")
    private String vaultHost;

    @Value("${sinqia.vault.token-header}")
    private String vaultTokenHeader;

    @Value("${sinqia.vault.token-value}")
    private String vaultTokenValue;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    PrivateKeyRepository privateKeyRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    public String encode(String payload) throws Exception {

        Parameter parameter = parameterRepository.findAll().stream().findFirst().orElseThrow(() ->
                new RuntimeException("Nenhuma parametrização encontrada para o usuário: " + TenantContext.getCurrentTenant()));

        VaultPrivateKey pk = privateKeyRepository.findPrivateKey(parameter.getIdEntPar()).
                orElseThrow(() -> new RuntimeException("Nenhuma chave privada encontrada para este cliente: " + TenantContext.getCurrentTenant()));

        return signPayloadWithNimbus(payload, pk.getDigitalCertificate().getId(), getPrivateKey(parameter.getIdEntPar(), pk.getId()));
    }

    private String signPayloadWithNimbus(String payload, Long kid, String privateKeyPem) throws JOSEException, URISyntaxException {
        JWK jwk = JWK.parseFromPEMEncodedObjects(privateKeyPem);

        RSAKey rsaKey = jwk.toRSAKey();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaKey);

        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(rsaKey.getKeyID())
                        .x509CertSHA256Thumbprint(rsaKey.computeThumbprint("SHA-1"))
                        .jwkURL(new URI(JWK_URL))
                        .keyID(String.valueOf(kid))
                        .build(),
                new Payload(Base64.encodeBase64URLSafe(payload.getBytes())));

        // Compute the RSA signature
        jwsObject.sign(signer);

        // Serialize the JWS to compact form
        return jwsObject.serialize();
    }

    private String getPrivateKey(Long participantyEntity, Long key) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(vaultTokenHeader, vaultTokenValue);

        String url = vaultHost + "/v1/secret/data/sinqia/pix/" + participantyEntity + "/" + key;

        ResponseEntity<PrivateKeyResponse> response = restTemplate.exchange(RequestEntity.get(new URI(url)).headers(headers).build(), PrivateKeyResponse.class);
        if (response.getBody() != null &&
                response.getBody().getData() != null &&
                response.getBody().getData().getData() != null) {
            return "-----BEGIN PRIVATE KEY-----\n" +
                    response.getBody().getData().getData().getChave() +
                    "\n-----END PRIVATE KEY-----";
        }
        return null;
    }
}
