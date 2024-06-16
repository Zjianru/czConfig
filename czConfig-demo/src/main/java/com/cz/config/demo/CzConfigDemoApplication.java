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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(LocalConfig.class)
@EnableCzConfig
@RestController
public class CzConfigDemoApplication {

    @Value("${cz.config.app}")
    private String configApp;
    @Value("${cz.config.env}")
    private String configEnv;
    @Value("${cz.config.ns}")
    private String configNs;

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
            System.out.println("yml app->"+configApp);
            System.out.println("yml env->"+configEnv);
            System.out.println("yml ns->"+configNs);
            System.out.println("ns-->" + localConfig.getNs());
            System.out.println("app-->" + localConfig.getApp());
            System.out.println("env-->" + localConfig.getEnv());
        };
    }

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String hello() {
        String string11 = "yml app->" + configApp;
        String string12 = "yml env->" + configEnv;
        String string13 = "yml ns->" + configNs;
        String string2 = "ns-->" + localConfig.getNs();
        String string3 = "app-->" + localConfig.getApp();
        String string4 = "env-->" + localConfig.getEnv();
        return Arrays.asList(string11, string12, string13, string2, string3, string4).toString();
    }
}
