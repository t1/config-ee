package com.github.t1.configee;

import static java.util.Arrays.*;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class ConfigCdiExtension implements Extension {
    private final Map<Field, ConfigValue<?>> configPoints = new HashMap<>();

    private final ConfigSource configSource = MultiConfigSource.load();

    public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
        Class<T> type = pit.getAnnotatedType().getJavaClass();
        log.trace("scan {} for config points", type);

        configPointsIn(type).forEach(field -> configPoints.put(field, createConfigValue(field)));

        pit.setInjectionTarget(new InjectionTargetWrapper<T>(pit.getInjectionTarget()) {
            @Override
            public T produce(CreationalContext<T> context) {
                T instance = super.produce(context);
                log.trace("configure instance: {}", instance);
                configPointsIn(instance.getClass()).forEach(field -> setConfigPoint(instance, field));
                return instance;
            }
        });
    }

    private <T> Stream<Field> configPointsIn(Class<T> type) {
        return asList(type.getDeclaredFields()).stream().filter(this::isConfigPoint);
    }

    private boolean isConfigPoint(Field field) {
        return ConfigValue.class.isAssignableFrom(field.getType());
    }

    private void setConfigPoint(Object instance, Field field) {
        try {
            ConfigValue<?> value = configPoints.get(field);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigValue<?> createConfigValue(Field field) {
        String name = field.getName();
        Class<?> type = typeArg0(field);
        log.debug("create {} config point '{}' in {}", type.getSimpleName(), name,
                field.getDeclaringClass().getSimpleName());
        return new ConfigValue<>(configSource, type, name);
    }

    private Class<?> typeArg0(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }
}
