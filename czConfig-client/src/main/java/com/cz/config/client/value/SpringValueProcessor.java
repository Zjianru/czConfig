package com.cz.config.client.value;

import cn.kimmking.utils.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.util.List;

/**
 * process spring value
 * 1. 扫描所有的 springvalue 注解 保存起来
 * 2. 监听配置中心
 * 3. 配置中心有变化，更新springvalue
 * 触发事件可注解或继承接口
 * 1. @EventListener 注解
 * 2. 接口参考org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder
 * 两种方式等价
 *
 * @author Zjianru
 */
public class SpringValueProcessor implements BeanPostProcessor, ApplicationListener<EnvironmentChangeEvent> {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        FieldUtils.findAnnotatedField(bean.getClass(), Value.class).forEach(field -> {
            Value annotation = field.getAnnotation(Value.class);
            String config = annotation.value();
        });




        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {

    }
}
