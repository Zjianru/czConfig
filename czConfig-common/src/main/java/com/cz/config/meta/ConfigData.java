package com.cz.config.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * code desc
 *
 * @author Zjianru
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigData {
    private Integer id;

    private String app;

    private String env;

    private String ns;

    private String properties;

    private String placeholder;
}
