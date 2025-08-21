package com.springboot.circuitbreaker.infrastructure.circuitbreaker.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitProtection {
    ProtectionTarget value();
}
