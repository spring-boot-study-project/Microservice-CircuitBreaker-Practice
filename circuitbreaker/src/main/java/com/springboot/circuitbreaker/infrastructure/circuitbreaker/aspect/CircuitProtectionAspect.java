package com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.provider.FallbackHandlerProvider;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Aspect
@Component
public class CircuitProtectionAspect {
    private final CircuitBreakerRegistry registry;
    private final FallbackHandlerProvider fallbackProvider;

    public CircuitProtectionAspect(CircuitBreakerRegistry registry, FallbackHandlerProvider fallbackProvider) {
        this.registry = registry;
        this.fallbackProvider = fallbackProvider;
    }

    @Around("@annotation(com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect.CircuitProtection)")
    public Object protect(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        CircuitProtection annotation = signature.getMethod().getAnnotation(CircuitProtection.class);
        ProtectionTarget target = annotation.value();
        String circuitBreakerName = target.getCircuitBreakerName();

        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);

        try {
            return circuitBreaker.executeCheckedSupplier(pjp::proceed);
        } catch (Throwable throwable) {
            return fallbackProvider.getHandler(target)
                    .map(handler -> handler.handle(throwable, pjp.getArgs()))
                    .orElseThrow(() -> throwable);
        }

    }
}
