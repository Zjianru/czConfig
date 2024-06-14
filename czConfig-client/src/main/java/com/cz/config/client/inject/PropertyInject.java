package com.cz.config.client.inject;

import java.util.Map;

/**
 * Injection property to client
 *
 * @author Zjianru
 */
public class PropertyInject implements PropertyType{

    Map<String, String> configs;

    public PropertyInject(Map<String, String> configs) {
        this.configs = configs;
    }



    @Override
    public String[] getPropertyNames() {
        return configs.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return configs.get(name);
    }
}
