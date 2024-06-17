package com.cz.config.server.util;

import com.cz.config.meta.ConfigData;
import com.cz.config.server.meta.Configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * code desc
 *
 * @author Zjianru
 */
public class TriggerUtils {

    /**
     * 将Configs对象列表转换为ConfigData对象列表。
     *
     * @param configs Configs对象列表
     * @return ConfigData对象列表
     */
    public static  List<ConfigData> transferToConfig(List<Configs> configs) {
        List<ConfigData> result = new ArrayList<>();
        configs.forEach(item -> {
            ConfigData configData = ConfigData.builder()
                    .app(item.getApp())
                    .env(item.getEnv())
                    .ns(item.getNs())
                    .properties(item.getProperties())
                    .placeholder(item.getPlaceholder())
                    .id(item.getId())
                    .build();
            result.add(configData);
        });
        return result;
    }

    /**
     * 将参数映射转换为Configs对象列表。
     *
     * @param app   应用名
     * @param env   环境
     * @param ns    命名空间
     * @param param 参数映射
     * @return Configs对象列表
     */
    public static List<Configs> transferParamToConfig(String app, String env, String ns, Map<String, String> param) {
        List<Configs> configs = new ArrayList<>();
        param.forEach((k, v) -> {
            Configs build = Configs.builder()
                    .properties(k)
                    .placeholder(v)
                    .ns(ns)
                    .app(app)
                    .env(env)
                    .build();
            configs.add(build);
        });
        return configs;
    }

}
