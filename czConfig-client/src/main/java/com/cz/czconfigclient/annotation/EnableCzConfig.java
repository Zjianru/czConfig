package com.cz.czconfigclient.annotation;

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
public @interface EnableCzConfig {
}
