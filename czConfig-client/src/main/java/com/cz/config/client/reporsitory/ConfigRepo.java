package com.cz.config.client.reporsitory;

import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.invoke.HttpRepo;
import org.springframework.context.ApplicationContext;

import javax.swing.event.ChangeEvent;
import java.util.Map;

/**
 * code desc
 *
 * @author Zjianru
 */
public interface ConfigRepo {
    static ConfigRepo getDefault(ApplicationContext applicationContext, ConfigMeta configMeta) {
        return new HttpRepo(applicationContext, configMeta);
    }

    Map<String, String> getConfig();

    void addListener(RepoChangeListener listener);


}
