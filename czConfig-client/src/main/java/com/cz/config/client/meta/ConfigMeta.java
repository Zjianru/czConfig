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

    public String generateKey() {
        return app + "-" + env + "-" + ns;
    }

    public String versionPath() {
        return path("/version");
    }

    public String listPath() {
        return path("/list");
    }


    private String path(String context) {
        String versionPath = getServerPath() + context;
        String app = "app=" + getApp();
        String env = "env=" + getEnv();
        String ns = "ns=" + getNs();
        return versionPath + "?" + app + "&" + env + "&" + ns;
    }
}
