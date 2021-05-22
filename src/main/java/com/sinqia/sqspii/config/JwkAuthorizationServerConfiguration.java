package com.sinqia.sqspii.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.sinqia.sqspii.context.TenantContext;
import com.sinqia.sqspii.data.multitenancy.entity.DigitalCertificate;
import com.sinqia.sqspii.data.multitenancy.entity.UsuarioDadosAcesso;
import com.sinqia.sqspii.data.multitenancy.repository.DigitalCertificateRepository;
import com.sinqia.sqspii.data.multitenancy.repository.UsuarioDadosAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class JwkAuthorizationServerConfiguration {

    @Autowired
    private DigitalCertificateRepository repository;

    @Autowired
    private UsuarioDadosAcessoRepository configRepo;

    @Bean
    public JWKSet jwkSet() throws Exception {
        List<UsuarioDadosAcesso> configList = configRepo.findAll();
        List<JWK> keys = new ArrayList<>();

        for (UsuarioDadosAcesso uda : configList) {
            TenantContext.setCurrentTenant(uda.getUserCode());
            List<DigitalCertificate> certificates;

            try {
                certificates = repository.findAll();
            } catch (Exception e) {
                continue;
            }

            certificates.forEach(cert -> {
                try {
                    X509Certificate certificate = parseCertificate(cert.getDescriptionCertificate());

                    if (certificate != null) {
                        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) getPublicKey(cert.getDescriptionCertificate()))
                                .keyUse(KeyUse.SIGNATURE)
                                .algorithm(JWSAlgorithm.RS256)
                                .x509CertThumbprint(Base64URL.encode(certificate.getEncoded()))
                                .keyID(String.valueOf(cert.getId()));
                        keys.add(builder.build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return new JWKSet(keys);
    }

    public static X509Certificate parseCertificate(String certStr) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certStr.getBytes()));
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

   /* public X509Certificate load509Certificate() throws Exception {
        try {

            InputStream resource = new ClassPathResource(
                    KEY_STORE_FILE).getInputStream();
            boolean isAliasWithPrivateKey = false;
            KeyStore keyStore = KeyStore.getInstance("JKS");

            // Provide location of Java Keystore and password for access
            keyStore.load(resource, KEY_STORE_PASSWORD.toCharArray());

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
    }*/

    public static PublicKey getPublicKey(String certificate) {
        return Objects.requireNonNull(parseCertificate(certificate)).getPublicKey();
    }

}
