package com.springboot.circuitbreaker.infrastructure.circuitbreaker.executor;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;

@Component
public class DefaultCircuitExecutor implements CircuitExecutor {

    private final CircuitBreakerRegistry registry;

    public DefaultCircuitExecutor(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T> Try<T> execute(ProtectionTarget target, Supplier<T> supplier) {
        String circuitBreakerName = target.getCircuitBreakerName();
        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);

        return Try.ofSupplier(circuitBreaker.decorateSupplier(supplier));
    }

    @Override
    public <T> Try<T> execute(ProtectionTarget target, Supplier<T> supplier, Function<Throwable, T> fallback) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(target.getCircuitBreakerName());
        return Try.ofSupplier(circuitBreaker.decorateSupplier(supplier))
                .recover(fallback);
    }
}
