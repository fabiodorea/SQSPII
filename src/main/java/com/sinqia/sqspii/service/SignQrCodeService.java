package com.sinqia.sqspii.service;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.sinqia.sqspii.config.JwkAuthorizationServerConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class SignQrCodeService {

    private static final String JWK_URL = "localhost:8100/sqspii/.well-known/jwks.json";

    @Autowired
    private JWKSet jwkSet;

    @Autowired
    private JwkAuthorizationServerConfiguration jwkAuthorizationServerConfiguration;

    public String encode(String payload) throws Exception {
        RSAKey rsaKey = jwkSet.getKeyByKeyId(JwkAuthorizationServerConfiguration.JWK_KID).toRSAKey();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaKey);

        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(rsaKey.getKeyID())
                        .x509CertSHA256Thumbprint(rsaKey.computeThumbprint("SHA-1"))
                        .jwkURL(new URI(JWK_URL))
                        .build(),
                new Payload(Base64.encodeBase64URLSafe(payload.getBytes())));

        // Compute the RSA signature
        jwsObject.sign(signer);

        // Serialize the JWS to compact form
        return jwsObject.serialize();
    }


/*    public String decode(String jweString) throws ParseException, IOException {

        // Load JWK set from URL
        JWKSet publicKeys = JWKSet.load(new URL("http://localhost/.well-known/jwks.json"));

        // Parse the JWE string
        JWEObject jweObject = JWEObject.parse(jweString);

        // Decrypt with private key
        jweObject.decrypt(new RSADecrypter(recipientJWK));

        // Extract payload
        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();


        assertNotNull("Payload not a signed JWT", signedJWT);

        // Check the signature
        assertTrue(signedJWT.verify(new RSASSAVerifier(senderPublicJWK)));

        // Retrieve the JWT claims...
        assertEquals("alice", signedJWT.getJWTClaimsSet().getSubject());
    }*/
}
