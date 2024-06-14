package com.cz.config.client.annotation;

import com.cz.config.client.registry.BeanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * annotation for enable config center
 *
 * @author Zjianru
 */
@Inherited
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import({BeanRegistrar.class})
public @interface EnableCzConfig {
}
