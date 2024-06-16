package com.cz.config.client.reporsitory;

import com.cz.config.client.meta.ConfigMeta;

import java.util.Map;

/**
 * code desc
 *
 * @author Zjianru
 */
public interface RepoChangeListener {

    void onChange(ChangeEvent event);

    record ChangeEvent(ConfigMeta meta, Map<String, String> config) {
    }

}