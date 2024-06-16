package com.cz.config.client.inject;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Injection property to client
 *
 * @author Zjianru
 */
public class PropertyInject implements PropertyType {

    Map<String, String> configs;

    ApplicationContext applicationContext;

    public PropertyInject(ApplicationContext applicationContext,Map<String, String> configs) {
        this.applicationContext = applicationContext;
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

    @Override
    public void onChange(ChangeEvent event) {
        this.configs = event.config();
        if (!configs.isEmpty()){
            // 发布变更事件
            applicationContext.publishEvent(new EnvironmentChangeEvent(configs.keySet()));
        }
    }
}
