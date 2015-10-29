package com.github.kpavlov.restws.server;

import com.github.kpavlov.restws.server.hmac.HmacFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Bean
    HmacFilter hmacFilter() {
        return new HmacFilter();
    }
}
