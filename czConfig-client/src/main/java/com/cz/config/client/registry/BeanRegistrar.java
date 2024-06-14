package com.cz.config.client.registry;

import com.cz.config.client.process.PropertySourcesProcess;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * register config bean to spring
 *
 * @author Zjianru
 */
public class BeanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
        System.out.println("come into registerBeanDefinitions");
        if (!registry.containsBeanDefinition(PropertySourcesProcess.class.getName())){
            System.out.println("start register cz config bean");
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(PropertySourcesProcess.class).getBeanDefinition();
            registry.registerBeanDefinition(PropertySourcesProcess.class.getName(), beanDefinition);
        }
    }
}
