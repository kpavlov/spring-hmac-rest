package com.github.kpavlov.restws.server.hmac;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HmacConfig {

    @Bean
    CredentialsProvider credentialsProvider() {
        return new SimpleCredentialsProvider("api", "secret");
    }
}
