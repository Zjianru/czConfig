package com.cz.config.server.meta;

import lombok.Builder;
import lombok.Data;

/**
* code desc
*
* @author Zjianru
*/
@Data
@Builder
public class Configs {
    private Integer id;

    private String app;

    private String env;

    private String ns;

    private String properties;

    private String placeholder;
}