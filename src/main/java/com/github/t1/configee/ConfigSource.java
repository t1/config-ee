package com.github.t1.configee;

public interface ConfigSource {

    <T> T get(Class<T> type, String key);

}
