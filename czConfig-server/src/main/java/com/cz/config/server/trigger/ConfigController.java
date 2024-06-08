package com.cz.config.server.trigger;

import com.cz.config.server.meta.Configs;
import com.cz.config.server.repo.ConfigsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * config server trigger
 *
 * @author Zjianru
 */
@RestController
@Slf4j
public class ConfigController {
    @Autowired
    ConfigsRepo configsRepo;

    private static final Map<String, Long> VERSION = new ConcurrentHashMap<>();

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Configs> list(@RequestParam("app") String app,
                              @RequestParam("env") String env,
                              @RequestParam("ns") String ns) {
        log.debug("function [list] param=> app is {} , env is {} , ns is {}", app, env, ns);
        // 添加异常处理
        try {
            return configsRepo.findByAppAndEnvAndNs(app, env, ns);
        } catch (Exception e) {
            log.error("Failed to fetch configs", e);
            throw new RuntimeException("Failed to fetch configs", e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public List<Configs> update(@RequestParam("app") String app,
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

        return configs;
    }

    /**
     * 本质上是全部删除后插入新数据
     *
     * @param app
     * @param env
     * @param ns
     * @param configs
     */
    private void saveOrUpdateBatch(String app, String env, String ns, List<Configs> configs) {
        List<Configs> needSave = new ArrayList<>();
        configs.forEach(item -> {
            List<Configs> value = configsRepo.findByAppAndEnvAndNsAndProperties(app, env, ns, item.getProperties());
            if (!value.isEmpty()) {
                int successLineCount = configsRepo.updateConfigsPlaceholder(item.getPlaceholder(), app, env, ns, item.getProperties());
                log.debug("update success ,affected row is {}",successLineCount);
            } else {
                needSave.add(item);
            }
        });
        if (!needSave.isEmpty()) {
            configsRepo.saveAll(needSave);
        }
    }


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

}
