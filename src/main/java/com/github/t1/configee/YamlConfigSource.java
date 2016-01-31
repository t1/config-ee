package com.github.t1.configee;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;

import org.yaml.snakeyaml.Yaml;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class YamlConfigSource implements ConfigSource {
    private static final Yaml YAML = new Yaml();

    @SneakyThrows(IOException.class)
    public static ConfigSource load(URL url) {
        try (InputStream stream = url.openStream()) {
            Object node = YAML.load(stream);
            YamlConfigSource configSource = new YamlConfigSource(node);
            log.debug("loaded from {}", url);
            return configSource;
        }
    }

    private final Object config;

    @Override
    @SneakyThrows(ReflectiveOperationException.class)
    public <T> T get(Class<T> type, String key) {
        Field field = config.getClass().getDeclaredField(key);
        field.setAccessible(true);
        Object value = field.get(config);
        return type.cast(value);
    }

}
