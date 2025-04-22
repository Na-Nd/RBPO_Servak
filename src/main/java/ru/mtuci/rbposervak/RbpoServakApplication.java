package ru.mtuci.rbposervak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableScheduling
public class RbpoServakApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbpoServakApplication.class, args);
    }

}
