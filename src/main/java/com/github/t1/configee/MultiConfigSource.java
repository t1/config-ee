package com.github.t1.configee;

import java.net.URL;

public class MultiConfigSource implements ConfigSource {
    private static MultiConfigSource instance = null;

    public static ConfigSource load() {
        if (instance == null)
            instance = new MultiConfigSource();
        return instance;
    }

    private final ConfigSource configSource =
            findConfigSource();

    private ConfigSource findConfigSource() {
        URL resource = ConfigSource.class.getResource("/config.yaml");
        if (resource != null)
            return YamlConfigSource.load(resource);
        resource = ConfigSource.class.getResource("/config.properties");
        if (resource != null)
            return PropertiesConfigSource.load(resource);
        throw new RuntimeException("no config source found");
    }

    @Override
    public <T> T get(Class<T> type, String key) {
        return configSource.get(type, key);
    }

}
