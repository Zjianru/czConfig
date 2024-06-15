package com.cz.config.client.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * code desc
 *
 * @author Zjianru
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConfigMeta {
    String app;
    String env;
    String ns;
    String serverPath;
}
