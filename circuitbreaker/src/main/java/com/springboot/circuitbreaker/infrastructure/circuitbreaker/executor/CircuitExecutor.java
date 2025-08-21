package com.springboot.circuitbreaker.infrastructure.circuitbreaker.executor;

import java.util.function.Function;
import java.util.function.Supplier;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

import io.vavr.control.Try;

public interface CircuitExecutor {
    <T> Try<T> execute(ProtectionTarget target, Supplier<T> supplier);

    <T> Try<T> execute(ProtectionTarget target, Supplier<T> supplier, Function<Throwable, T> fallback);
}
