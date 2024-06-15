package com.cz.config.demo;

import com.cz.config.client.annotation.EnableCzConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(LocalConfig.class)
@EnableCzConfig
public class CzConfigDemoApplication {

    @Value("${cz.config.app}")
    private String configValueApp;
    @Autowired
    private LocalConfig localConfig;
    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(CzConfigDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(Arrays.toString(environment.getActiveProfiles()));
            System.out.println("configValueApp->"+configValueApp);
            System.out.println("ns-->" + localConfig.getNs());
            System.out.println("app-->" + localConfig.getApp());
            System.out.println("env-->" + localConfig.getEnv());
        };
    }
}
