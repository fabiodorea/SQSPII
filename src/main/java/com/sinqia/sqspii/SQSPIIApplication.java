package com.sinqia.sqspii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@EnableFeignClients
public class SQSPIIApplication {

    public static void main(String[] args) {
        SpringApplication.run(SQSPIIApplication.class, args);
    }

}
