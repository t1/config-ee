package com.github.t1.configee;

import static java.util.Arrays.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import org.joda.convert.StringConvert;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigCdiExtension implements Extension {
    public static final StringConvert CONVERT = new StringConvert();

    private final Map<Field, ConfigValue<?>> configPoints = new HashMap<>();
    private final Properties properties = new Properties();

    @SneakyThrows(IOException.class)
    public ConfigCdiExtension() {
        properties.load(getClass().getResourceAsStream("/config.properties"));
        log.debug("loaded {}", properties);
    }

    public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
        Class<T> type = pit.getAnnotatedType().getJavaClass();
        log.trace("scan {} for config points", type);

        configPointsIn(type).forEach(field -> configPoints.put(field, createConfigValue(field)));

        pit.setInjectionTarget(new InjectionTargetWrapper<T>(pit.getInjectionTarget()) {
            @Override
            public T produce(CreationalContext<T> context) {
                T instance = super.produce(context);
                log.trace("created: {}", instance);
                configPointsIn(instance.getClass()).forEach(field -> setConfigPoint(instance, field));
                return instance;
            }
        });
    }

    private <T> Stream<Field> configPointsIn(Class<T> type) {
        return asList(type.getDeclaredFields()).stream()
                .filter(field -> isConfigPoint(field));
    }

    private boolean isConfigPoint(Field field) {
        return ConfigValue.class.isAssignableFrom(field.getType());
    }

    private void setConfigPoint(Object instance, Field field) {
        try {
            ConfigValue<?> value = configPoints.get(field);
            log.debug("set {}#{} to {}", instance, field.getName(), value);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigValue<?> createConfigValue(Field field) {
        String name = field.getName();
        Class<?> type = typeArg0(field);
        log.debug("create config point '{}' for {}#{}", name, type, field.getDeclaringClass().getSimpleName());
        return new ConfigValue<>(properties, type, name);
    }

    private Class<?> typeArg0(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }
}
