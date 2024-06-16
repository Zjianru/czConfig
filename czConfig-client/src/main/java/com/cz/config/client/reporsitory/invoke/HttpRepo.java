package com.cz.config.client.reporsitory.invoke;

import cn.kimmking.utils.HttpUtils;
import com.alibaba.fastjson.TypeReference;
import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.ConfigRepo;
import com.cz.config.client.reporsitory.RepoChangeListener;
import com.cz.config.meta.ConfigData;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 配置仓库的HTTP实现类，用于通过HTTP协议获取和更新配置数据。
 */
public class HttpRepo implements ConfigRepo {

    /**
     * 配置元数据，包含配置的标识和访问路径等信息。
     */
    private ConfigMeta configMeta;

    /**
     * 定时任务执行器，用于定时心跳检测和更新配置。
     */
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * 用于记录配置的版本信息，以便于版本比较和更新。
     */
    Map<String, Long> versionCompare = new HashMap<>();

    /**
     * 保存配置数据，以配置标识为键，配置内容为值。
     */
    Map<String, Map<String, String>> configDats = new HashMap<>();

    List<RepoChangeListener> changeListeners = new ArrayList<>();
    /**
     * 构造函数，初始化配置仓库。
     *
     * @param configMeta 配置元数据，用于初始化配置仓库。
     * @param applicationContext Spring应用上下文，用于获取配置数据。
     */
    public HttpRepo(ApplicationContext applicationContext,ConfigMeta configMeta) {
        this.configMeta = configMeta;
        // 初始化定时任务，每5秒执行一次心跳检测。
        executor.scheduleWithFixedDelay(this::heartBeat, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addListener(RepoChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * 获取当前配置。
     *
     * @return 当前配置的数据映射。
     */
    @Override
    public Map<String, String> getConfig() {
        // 生成配置的唯一标识。
        String configKey = configMeta.generateKey();
        // 如果缓存中已存在配置，则直接返回。
        if (configDats.containsKey(configKey)) {
            return configDats.get(configKey);
        }
        // 缓存中不存在，需要从远程更新配置。
        System.out.println("[czConfig]---- cache not found --> need update configs ... ");
        return findAll();
    }

    /**
     * 从远程获取所有配置数据并返回为键值对映射。
     * <p>
     * 该方法通过发送HTTP GET请求，从指定的URL获取配置数据列表。然后，它遍历这些数据，
     * 将每个配置项的属性作为键，占位符作为值，存储在HashMap中。最后，返回这个包含所有配置项的映射。
     *
     * @return Map<String, String> 包含所有配置项的映射，其中键是配置项的属性，值是配置项的占位符。
     */
    private Map<String, String> findAll() {
        // 通过HTTP GET请求获取远程配置数据列表。
        // 通过HTTP请求获取远程配置数据。
        List<ConfigData> remoteData = HttpUtils.httpGet(configMeta.listPath(), new TypeReference<List<ConfigData>>() {
        });

        // 初始化一个HashMap来存储配置数据。
        // 将获取的配置数据转换为键值对映射。
        Map<String, String> configs = new HashMap<>();
        // 遍历远程配置数据列表，将每个配置项的属性和占位符添加到configs映射中。
        remoteData.forEach(item -> configs.put(item.getProperties(), item.getPlaceholder()));
        // 返回包含所有配置项的映射。
        // 更新缓存并返回配置。
        return configs;
    }


    /**
     * 心跳检测方法，用于定时检查配置是否有更新。
     */
    private void heartBeat() {
        // 通过HTTP请求获取最新的配置版本号。
        Long newVersion = HttpUtils.httpGet(configMeta.versionPath(), new TypeReference<Long>() {
        });
        // 生成配置的唯一标识。
        String configKey = configMeta.generateKey();
        // 获取之前记录的配置版本号。
        Long oldVersion = versionCompare.getOrDefault(configKey, -1L);
        // 比较新旧版本号，如果版本有更新，则更新配置。
        if (newVersion > oldVersion) {
            // 输出版本更新信息。
            System.out.println("[czConfig]---- new version is " + newVersion + ".. old version is " + oldVersion);
            System.out.println("[czConfig]---- version change--> need update configs ... ");
            // 更新版本号记录。
            versionCompare.put(configKey, newVersion);
            Map<String, String> all = findAll();
            // 更新配置缓存。
            configDats.put(configKey, all);
            System.out.println("[czConfig]---- fire environment change event ... ");
            // 修改源并通知 spring 发布修改事件
            changeListeners.forEach(listener -> listener.onChange(new RepoChangeListener.ChangeEvent(configMeta, all)));
        }
    }
}
