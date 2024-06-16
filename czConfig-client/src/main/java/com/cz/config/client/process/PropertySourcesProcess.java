package com.cz.config.client.process;

import com.cz.config.client.annotation.CzConfigPropertySource;
import com.cz.config.client.inject.PropertyType;
import com.cz.config.client.meta.ConfigKeyWords;
import com.cz.config.client.meta.ConfigMeta;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * get property from server and inject config
 * inject must early
 *
 * @author Zjianru
 */
@Setter
public class PropertySourcesProcess implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware, ApplicationContextAware {

    private final static String CZ_CONFIGS_PREFIX = "cz";
    private final static String CZ_CONFIG_PREFIX = "config";
    private final static String CONFIGS_PREFIX = CZ_CONFIGS_PREFIX + "." + CZ_CONFIG_PREFIX;

    Environment environment;
    ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        if (configurableEnvironment.getPropertySources().contains(CZ_CONFIG_PREFIX)) {
            return;
        }

        ConfigMeta configs = transferConfigMeta(configurableEnvironment);
        PropertyType propertyType = PropertyType.getDefault(applicationContext,configs);
        CzConfigPropertySource czConfigPropertySource = new CzConfigPropertySource(CZ_CONFIG_PREFIX, propertyType);
        // start inject to environment
        CompositePropertySource composite = new CompositePropertySource(CZ_CONFIGS_PREFIX);
        composite.addPropertySource(czConfigPropertySource);
        configurableEnvironment.getPropertySources().addFirst(composite);
    }

    private ConfigMeta transferConfigMeta(ConfigurableEnvironment configurableEnvironment) {
        String app = transProperty(configurableEnvironment, ConfigKeyWords.APP, "app1");
        String env = transProperty(configurableEnvironment, ConfigKeyWords.ENV, "dev");
        String ns = transProperty(configurableEnvironment, ConfigKeyWords.NS, "public");
        String server = transProperty(configurableEnvironment, ConfigKeyWords.SERVER, "http://localhost:7001");
        return ConfigMeta.builder().app(app).env(env).ns(ns).serverPath(server).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String transProperty(ConfigurableEnvironment configurableEnvironment, ConfigKeyWords property, String defaultValue) {
        return configurableEnvironment.getProperty(CONFIGS_PREFIX + ConfigKeyWords.SPLIT + property, defaultValue);
    }
}
