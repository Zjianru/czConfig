package com.cz.config.client.inject;

import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.ConfigRepo;

/**
 * code desc
 *
 * @author Zjianru
 */
public interface PropertyType {

    static PropertyType getDefault(ConfigMeta configMeta) {
        ConfigRepo repo = ConfigRepo.getDefault(configMeta);
        return new PropertyInject(repo.getConfig());
    }

    String[] getPropertyNames();

    String getProperty(String name);
}
