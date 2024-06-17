package com.cz.config.client.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * SpringValue 类用于封装与 Spring 注解相关的值信息。
 * 包含了对 bean、bean 名称、键、占位符和字段的引用，这些信息对于处理和解析 Spring 注解非常有用。
 *
 * @author Zjianru
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpringValue {
    // 相关的 bean 对象，它可能被注解标记并需要进行处理。
    private Object bean;
    // bean 的名称，用于在 Spring 应用上下文中唯一标识一个 bean。
    private String beanName;
    // 注解中的键，对应于注解属性的名称。
    private String key;
    // 占位符，用于在配置中提供默认值或进行值的替换。
    private String placeholder;
    // 相关的字段对象，如果注解应用于字段，则此字段包含有关字段的详细信息。
    private Field field;
}
