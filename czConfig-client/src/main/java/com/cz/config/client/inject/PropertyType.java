package com.cz.config.client.inject;

import com.cz.config.client.meta.ConfigMeta;
import com.cz.config.client.reporsitory.ConfigRepo;
import com.cz.config.client.reporsitory.RepoChangeListener;
import org.springframework.context.ApplicationContext;

/**
 * code desc
 *
 * @author Zjianru
 */
public interface PropertyType extends RepoChangeListener {

    static PropertyType getDefault(ApplicationContext applicationContext,ConfigMeta configMeta) {
        ConfigRepo repo = ConfigRepo.getDefault(applicationContext,configMeta);
        PropertyInject propertyInject = new PropertyInject(applicationContext, repo.getConfig());
        repo.addListener(propertyInject);
        return propertyInject;
    }

    String[] getPropertyNames();

    String getProperty(String name);
}
