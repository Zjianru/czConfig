package com.cz.config.client.reporsitory.invoke;

import cn.kimmking.utils.HttpUtils;
import com.alibaba.fastjson.TypeReference;
import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.ConfigRepo;
import com.cz.config.meta.ConfigData;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * code desc
 *
 * @author Zjianru
 */
@AllArgsConstructor
public class HttpRepo implements ConfigRepo {
    private ConfigMeta configMeta;


//    List<ConfigData> list( String app,  String env,  String ns)

    @Override
    public Map<String, String> getConfig() {
        Map<String, String> configs = new HashMap<>();
        String listPath = configMeta.getServerPath() + "/list";
        String app = "app=" + configMeta.getApp();
        String env = "env=" + configMeta.getEnv();
        String ns = "ns=" + configMeta.getNs();

        String httpPath = listPath + "?" + app + "&" + env + "&" + ns;
        List<ConfigData> remoteData = HttpUtils.httpGet(httpPath, new TypeReference<List<ConfigData>>() {});
        remoteData.forEach(item-> configs.put(item.getProperties(), item.getPlaceholder()));
        return configs;
    }
}
