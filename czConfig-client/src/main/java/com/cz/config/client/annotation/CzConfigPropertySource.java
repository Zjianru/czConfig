package com.cz.config.client.annotation;

import com.cz.config.client.inject.PropertyType;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * code desc
 *
 * @author Zjianru
 */
public class CzConfigPropertySource extends EnumerablePropertySource<PropertyType> {

    public CzConfigPropertySource(String name, PropertyType source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    @Override
    public Object getProperty(String name) {
        return source.getProperty(name);
    }
}
