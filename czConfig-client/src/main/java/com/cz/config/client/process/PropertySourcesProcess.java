package com.cz.config.client.process;

import com.cz.config.client.annotation.CzConfigPropertySource;
import com.cz.config.client.inject.PropertyInject;
import com.cz.config.client.inject.PropertyType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * get property from server and inject config
 * inject must early
 *
 * @author Zjianru
 */

public class PropertySourcesProcess implements BeanFactoryPostProcessor , PriorityOrdered, EnvironmentAware {

    private final static String CZ_CONFIG_PREFIX = "config";
    private final static String CZ_CONFIGS_PREFIX = "cz";
    Environment environment;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if(env.getPropertySources().contains(CZ_CONFIG_PREFIX)){
           return;
        }

        // TODO start get config from config server
        Map<String, String> configs = new HashMap<>();
        configs.put("cz.config.key1", "init by config server , value is key11111");
        configs.put("cz.config.key2", "init by config server , value is key22222");
        PropertyType propertyType = new PropertyInject(configs);
        CzConfigPropertySource czConfigPropertySource = new CzConfigPropertySource(CZ_CONFIG_PREFIX, propertyType);
        // start inject to environment
        CompositePropertySource composite = new CompositePropertySource(CZ_CONFIGS_PREFIX);
        composite.addPropertySource(czConfigPropertySource);
        env.getPropertySources().addFirst(composite);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
