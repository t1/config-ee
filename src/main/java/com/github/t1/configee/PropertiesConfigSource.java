package com.github.t1.configee;

import static lombok.AccessLevel.*;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class PropertiesConfigSource implements ConfigSource {
    private final Properties properties = new Properties();

    @SneakyThrows(IOException.class)
    public static PropertiesConfigSource load(URL url) {
        try (InputStream stream = url.openStream()) {
            PropertiesConfigSource configSource = new PropertiesConfigSource();
            configSource.properties.load(stream);
            log.debug("loaded from {}", url);
            return configSource;
        }
    }

    @Override
    public <T> T get(Class<T> type, String key) {
        String stringValue = properties.getProperty(key);
        T value = Converter.fromString(type, stringValue);
        return value;
    }

}
