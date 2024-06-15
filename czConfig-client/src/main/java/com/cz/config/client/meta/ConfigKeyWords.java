package com.cz.config.client.meta;

import lombok.Getter;

/**
 * code desc
 *
 * @author Zjianru
 */
@Getter
public enum ConfigKeyWords {
    APP("app"),
    ENV("env"),
    NS("ns"),
    SERVER("server"),
    SPLIT("."),
    ;

    private String key;

    ConfigKeyWords(String key) {
        this.key = key;
    }

}
