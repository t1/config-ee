package com.github.t1.configee;

import javax.enterprise.inject.spi.InjectionTarget;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public class InjectionTargetWrapper<T> implements InjectionTarget<T> {
    @Delegate
    private final InjectionTarget<T> delegate;
}
