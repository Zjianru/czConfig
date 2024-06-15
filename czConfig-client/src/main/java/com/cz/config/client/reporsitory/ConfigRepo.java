package com.cz.config.client.reporsitory;

import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.invoke.HttpRepo;

import java.util.Map;

/**
 * code desc
 *
 * @author Zjianru
 */
public interface ConfigRepo {
    static ConfigRepo getDefault(ConfigMeta configMeta) {
        return new HttpRepo(configMeta);
    }

    Map<String, String> getConfig();
}
