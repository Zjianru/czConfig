package com.cz.config.client.registry;

import com.cz.config.client.process.PropertySourcesProcess;
import com.cz.config.client.value.SpringValueProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * register config bean to spring
 * 实现ImportBeanDefinitionRegistrar接口，用于在Spring应用程序上下文中注册自定义bean。
 * 此类的作用是确保特定的类作为bean被注册到Spring的bean定义注册表中。
 *
 * @author Zjianru
 */
public class BeanRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 根据导入的类的元数据注册bean定义。
     * 此方法会注册PropertySourcesProcess和SpringValueProcessor两个类作为bean。
     *
     * @param importingClassMetadata 导入类的元数据，用于获取注解信息等。
     * @param registry               bean定义注册表，用于注册新的bean定义。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerClass(registry, PropertySourcesProcess.class);
        registerClass(registry, SpringValueProcessor.class);
    }

    /**
     * 注册指定的类为bean定义。
     * 如果指定的类已经注册为bean定义，则不会进行重复注册。
     *
     * @param registry bean定义注册表，用于注册新的bean定义。
     * @param aClass   要注册的类。
     */
    private static void registerClass(BeanDefinitionRegistry registry, Class<?> aClass) {
        // 打印开始注册bean的信息
        System.out.println("start register cz config bean");
        // 打印注册的bean类名
        System.out.println("[czConfig] register bean " + aClass.getName());
        // 检查当前bean定义注册表中是否已存在指定名称的bean定义
        if (!registry.containsBeanDefinition(aClass.getName())) {
            // 如果不存在，则创建一个新的bean定义并注册
            BeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(aClass).getBeanDefinition();
            registry.registerBeanDefinition(aClass.getName(), beanDefinition);
            // 打印bean注册完成的信息
            System.out.println("[czConfig] bean finish registered");
        } else {
            // 如果已经存在，则打印bean已注册的信息
            System.out.println("[czConfig] bean already registered");
        }
    }
}

