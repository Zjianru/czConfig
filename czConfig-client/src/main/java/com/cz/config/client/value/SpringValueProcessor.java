package com.cz.config.client.value;

import cn.kimmking.utils.FieldUtils;
import com.cz.config.client.util.PlaceholderHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
@Setter
@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<EnvironmentChangeEvent> {

    static final PlaceholderHelper helper = PlaceholderHelper.getInstance();
    static final MultiValueMap<String, SpringValue> VALUE_HOLDER = new LinkedMultiValueMap<>();

    private BeanFactory beanFactory;

    /**
     * 在bean初始化之前进行处理。
     * 此方法的目的是为了查找并处理被Value注解标记的字段，将这些字段的占位符键提取出来，
     * 并创建相应的SpringValue对象以供后续使用。
     *
     * @param bean     待处理的bean对象。
     * @param beanName bean的名称。
     * @return 处理后的bean对象。
     * @throws BeansException 如果处理过程中出现异常。
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 查找并遍历bean类中被Value注解标记的字段
        FieldUtils.findAnnotatedField(bean.getClass(), Value.class).forEach(field -> {
            log.info("find @Value annotation in field:{}", field.getName());
            // 获取字段上的Value注解
            Value annotation = field.getAnnotation(Value.class);
            // 提取注解中占位符键，并对每个键进行处理
            helper.extractPlaceholderKeys(annotation.value()).forEach(key -> {
                log.info("extract placeholder key:{}", key);
                // 创建SpringValue对象，用于存储bean、beanName、占位符键等信息
                SpringValue springValue = new SpringValue(bean, beanName, key, annotation.value(), field);
                // 将占位符键及其对应的SpringValue对象添加到VALUE_HOLDER中
                VALUE_HOLDER.add(key, springValue);
            });
        });
        return bean;
    }


    /**
     * 处理环境变量变化事件。
     * 当应用程序的环境变量发生改变时，此方法会被调用。它通过遍历所有变化的键，
     * 并尝试更新与这些键相关的Spring值，以确保应用程序能够根据新的环境变量值做出相应的调整。
     *
     * @param event 环境变化事件对象，包含所有发生变化的键和新的值。
     */
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        // 遍历所有发生变化的键
        event.getKeys().forEach(key -> {
            // 记录环境变化的键，用于调试和日志记录
            log.info("environment change event key:{}", key);
            // 尝试获取与当前键相关的Spring值列表
            List<SpringValue> springValues = VALUE_HOLDER.get(key);
            // 如果列表为空或不存在，则跳过当前键的处理
            if (springValues == null | springValues.isEmpty()) {
                return;
            }
            // 遍历与键相关的所有Spring值，尝试更新它们
            springValues.forEach(springValue -> {
                try {
                    // 记录开始更新Spring值的信息，用于调试和日志记录
                    log.info("start update spring value:{}", springValue);
                    // 解析新的值，并准备更新Spring字段
                    Object value = helper.resolvePropertyValue((ConfigurableBeanFactory) beanFactory, springValue.getBeanName(), springValue.getPlaceholder());
                    // 设置字段可访问，以便能够更新其值
                    springValue.getField().setAccessible(true);
                    // 更新Spring字段的值
                    springValue.getField().set(springValue.getBean(), value);
                } catch (IllegalAccessException e) {
                    // 如果在更新过程中发生访问异常，记录错误信息
                    log.error("update spring value error", e);
                }
            });
        });
    }

}
