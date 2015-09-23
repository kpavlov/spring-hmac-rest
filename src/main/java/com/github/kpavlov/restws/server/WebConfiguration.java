package com.github.kpavlov.restws.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Bean
    AbstractRequestLoggingFilter requestLoggingFilter() {
        final AbstractRequestLoggingFilter loggingFilter = new ServletContextRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }
}
