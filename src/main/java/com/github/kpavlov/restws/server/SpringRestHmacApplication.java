package com.github.kpavlov.restws.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = SpringRestHmacApplication.class)
public class SpringRestHmacApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRestHmacApplication.class, args);
    }
}
