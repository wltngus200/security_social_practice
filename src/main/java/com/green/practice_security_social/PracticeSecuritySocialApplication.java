package com.green.practice_security_social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PracticeSecuritySocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeSecuritySocialApplication.class, args);
    }

}
