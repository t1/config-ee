package com.github.t1.configee;

import java.util.Properties;

import lombok.*;

@RequiredArgsConstructor
public class ConfigValue<T> {
    private final Properties properties;
    @Getter
    private final Class<T> type;
    @Getter
    private final String key;

    public T get() {
        String string = properties.getProperty(key);
        T value = ConfigCdiExtension.CONVERT.convertFromString(type, string);
        return value;
    }

    @Override
    public String toString() {
        return "ConfigValue(" + key + ")";
    }
}
