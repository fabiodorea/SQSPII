package com.sinqia.sqspii.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;

@Configuration
@EnableAuthorizationServer
public class JwkAuthorizationServerConfiguration {

    private static final String KEY_STORE_FILE = "sqsp-jwt.jks";
    private static final String KEY_STORE_PASSWORD = "sqsp-pass";
    private static final String KEY_ALIAS = "sqsp-oauth-jwt";
    public static final String JWK_KID = "sqsp-key-id";

    @Bean
    public KeyPair keyPair() {
        ClassPathResource ksFile = new ClassPathResource(KEY_STORE_FILE);
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, KEY_STORE_PASSWORD.toCharArray());
        return ksFactory.getKeyPair(KEY_ALIAS);
    }

    @Bean
    public JWKSet jwkSet() throws Exception {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .privateKey((RSAPrivateKey) keyPair().getPrivate())
                .x509CertThumbprint(Base64URL.encode((load509Certificate()).getEncoded()))
                .keyID(JWK_KID);

        return new JWKSet(builder.build());
    }

    public X509Certificate load509Certificate() throws Exception {
        try {

            File resource = new ClassPathResource(
                    KEY_STORE_FILE).getFile();
            boolean isAliasWithPrivateKey = false;
            KeyStore keyStore = KeyStore.getInstance("JKS");

            // Provide location of Java Keystore and password for access
            keyStore.load(new FileInputStream(resource), KEY_STORE_PASSWORD.toCharArray());

            // iterate over all aliases
            Enumeration<String> es = keyStore.aliases();
            String alias = "";
            while (es.hasMoreElements()) {
                alias = es.nextElement();
                // if alias refers to a private key break at that point
                // as we want to use that certificate
                if (isAliasWithPrivateKey = keyStore.isKeyEntry(alias)) {
                    break;
                }
            }

            if (isAliasWithPrivateKey) {
                // Load certificate chain
                Certificate[] chain = keyStore.getCertificateChain(alias);
                return (X509Certificate) chain[0];
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        throw new Exception("Fail to load 509Certificate");
    }

}
