package com.github.t1.configee;

import lombok.*;

@RequiredArgsConstructor
public class ConfigValue<T> {
    private final ConfigSource configSource;
    @Getter
    private final Class<T> type;
    @Getter
    private final String key;

    public T get() {
        return configSource.get(type, key);
    }

    @Override
    public String toString() {
        return "ConfigValue(" + type + ":" + key + ")";
    }
}
