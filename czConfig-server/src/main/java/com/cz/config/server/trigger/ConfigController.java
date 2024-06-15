package com.cz.config.server.trigger;

import com.cz.config.meta.ConfigData;
import com.cz.config.server.meta.Configs;
import com.cz.config.server.repo.ConfigsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置服务器触发器控制器，负责处理与配置数据相关的HTTP请求。
 */
@RestController
@Slf4j
public class ConfigController {
    @Autowired
    private ConfigsMapper configsMapper;

    /**
     * 用于存储配置项的最新版本信息的映射。
     */
    private static final Map<String, Long> VERSION = new ConcurrentHashMap<>();

    @Autowired
    private DataSourceTransactionManager transactionManager;

    /**
     * 根据应用名、环境和命名空间获取配置列表。
     *
     * @param app 应用名
     * @param env 环境
     * @param ns  命名空间
     * @return 配置数据列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ConfigData> list(@RequestParam("app") String app,
                                 @RequestParam("env") String env,
                                 @RequestParam("ns") String ns) {
        log.debug("function [list] param=> app is {} , env is {} , ns is {}", app, env, ns);
        // 添加异常处理
        try {
            return transferToConfig(configsMapper.findByAppAndEnvAndNs(app, env, ns));
        } catch (Exception e) {
            log.error("Failed to fetch configs", e);
            throw new RuntimeException("Failed to fetch configs", e);
        }
    }

    /**
     * 更新配置项。
     *
     * @param app   应用名
     * @param env   环境
     * @param ns    命名空间
     * @param param 包含配置项键值对的映射
     * @return 更新后的配置数据列表
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public List<ConfigData> update(@RequestParam("app") String app,
                                   @RequestParam("env") String env,
                                   @RequestParam("ns") String ns,
                                   @RequestBody Map<String, String> param) {
        log.debug("function [update] param=> app is {} , env is {} , ns is {} , param is {}", app, env, ns, param);
        // 添加输入验证逻辑（示例性代码，需要根据实际情况设计）
        if (param.containsKey("invalidKey")) {
            throw new IllegalArgumentException("Invalid key detected");
        }

        List<Configs> configs = transferParamToConfig(app, env, ns, param);

        // 日志输出和业务逻辑分离
        log.debug("start modify config data ...");
        saveOrUpdateBatch(app, env, ns, configs);

        log.debug("start modify version data ...");
        VERSION.put(app + env + ns, System.currentTimeMillis());

        return transferToConfig(configs);
    }

    /**
     * 获取指定应用、环境和命名空间的配置版本。
     *
     * @param app 应用名
     * @param env 环境
     * @param ns  命名空间
     * @return 配置版本
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Long version(@RequestParam("app") String app,
                        @RequestParam("env") String env,
                        @RequestParam("ns") String ns) {
        log.debug("function [version] param=> app is {} , env is {} , ns is {}", app, env, ns);
        // 添加异常处理
        try {
            return VERSION.getOrDefault(app + env + ns, -1L);
        } catch (Exception e) {
            log.error("Failed to fetch version", e);
            throw new RuntimeException("Failed to fetch version", e);
        }
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
    private List<Configs> transferParamToConfig(String app, String env, String ns, Map<String, String> param) {
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

    /**
     * 将Configs对象列表转换为ConfigData对象列表。
     *
     * @param configs Configs对象列表
     * @return ConfigData对象列表
     */
    private List<ConfigData> transferToConfig(List<Configs> configs) {
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
     * 批量保存或更新配置项。
     *
     * @param app     应用名
     * @param env     环境
     * @param ns      命名空间
     * @param configs 配置项列表
     */
    private void saveOrUpdateBatch(String app, String env, String ns, List<Configs> configs) {
        List<Configs> needSave = new ArrayList<>();
        configs.forEach(item -> {
            List<Configs> value = configsMapper.findByAppAndEnvAndNsAndProperties(app, env, ns, item.getProperties());
            if (!value.isEmpty()) {
                int successLineCount = configsMapper.updatePlaceholderByOther(item.getPlaceholder(), app, env, ns, item.getProperties());
                log.debug("update success ,affected row is {}", successLineCount);
            } else {
                needSave.add(item);
            }
        });
        if (!needSave.isEmpty()) {
            configsMapper.batchInsert(needSave);
        }
    }
}
