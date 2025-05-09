package ru.mtuci.rbposervak.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeysConfig {

    @Value("${security.rsa.private-key}")
    private String privateKeyPem;

    @Value("${security.rsa.public-key}")
    private String publicKeyPem;

    @Bean
    public KeyPair keyPair() {
        try {

            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(privateKeyPem)
            );
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(
                    Base64.getDecoder().decode(publicKeyPem)
            );

            return new KeyPair(
                    kf.generatePublic(publicSpec),
                    kf.generatePrivate(privateSpec)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load keys", e);
        }
    }

    @Bean
    public PublicKey publicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    @Bean
    public PrivateKey privateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }
}
