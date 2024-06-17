package com.cz.config.client.inject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Injection property to client
 *
 * @author Zjianru
 */
@Slf4j
public class PropertyInject implements PropertyType {

    Map<String, String> configs;

    ApplicationContext applicationContext;

    public PropertyInject(ApplicationContext applicationContext, Map<String, String> configs) {
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

    /**
     * 处理配置变更事件。
     * 当配置发生变化时，此方法被调用。它比较新旧配置，识别出发生变化的配置项，并根据变化情况触发环境变更事件。
     *
     * @param event 配置变更事件，包含新的配置信息。
     */
    @Override
    public void onChange(ChangeEvent event) {
        // 获取最新的配置信息
        Map<String, String> newConfigs = event.config();
        // 计算与旧配置相比，哪些配置键的值发生了变化
        Set<String> changedKeys = calculateChangedValue(this.configs, newConfigs);
        // 如果没有配置项发生变化，则不进行后续处理
        if (changedKeys.isEmpty()) {
            log.info("calculate changedKeys return empty .... skip change config");
            return;
        }
        // 更新当前配置为最新的配置
        this.configs = newConfigs;
        // 如果有配置项发生变化，则触发环境变更事件
        if (!configs.isEmpty()) {
            // 发布变更事件
            log.info("fire env changed event --> config changed:{}", changedKeys);
            applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
        }
    }


    /**
     * 计算两个配置映射之间的差异，返回在新配置中被修改或新增的键集合。
     *
     * @param oldConfigs 旧的配置映射，包含之前的配置键值对。
     * @param newConfig  新的配置映射，包含最新的配置键值对。
     * @return 返回一个集合，包含所有在新配置中被修改或新增的键。
     */
    private Set<String> calculateChangedValue(Map<String, String> oldConfigs, Map<String, String> newConfig) {
        // 如果旧配置为空，说明所有新配置的键都是新增的
        if (oldConfigs.isEmpty()) return newConfig.keySet();
        // 如果新配置为空，说明所有旧配置的键都是被修改的（因为新配置没有这些键）
        if (newConfig.isEmpty()) return oldConfigs.keySet();

        // 计算在新配置中键值对被修改的键集合
        Set<String> news = newConfig.keySet().stream()
                .filter(key -> !newConfig.get(key).equals(oldConfigs.get(key)))
                .collect(Collectors.toSet());
        // 添加在新配置中但不在旧配置中出现的键到集合中
        oldConfigs.keySet().stream().filter(key -> !newConfig.containsKey(key)).forEach(news::add);
        return news;
    }

}
