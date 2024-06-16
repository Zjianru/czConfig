package com.cz.config.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * code desc
 *
 * @author Zjianru
 */
@ConfigurationProperties(prefix = "cz.config")
@Data
public class LocalConfig {
    String app;
    String env;
    String ns;
}
