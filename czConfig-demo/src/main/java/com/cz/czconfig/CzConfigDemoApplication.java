package com.cz.czconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(LocalConfig.class)
public class CzConfigDemoApplication {

    @Value("${cz.config.key1}")
    private String configValue;
    @Autowired
    private LocalConfig localConfig;

    public static void main(String[] args) {
        SpringApplication.run(CzConfigDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(configValue);
            System.out.println(localConfig.getKey1());
        };
    }
}
