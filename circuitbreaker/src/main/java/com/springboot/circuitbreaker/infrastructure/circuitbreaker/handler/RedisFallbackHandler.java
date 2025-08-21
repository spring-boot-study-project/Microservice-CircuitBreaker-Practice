package com.springboot.circuitbreaker.infrastructure.circuitbreaker.handler;

import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisFallbackHandler implements FallbackHandler {

    @Override
    public ProtectionTarget getTarget() {
        return ProtectionTarget.REDIS;
    }

    @Override
    public Object handle(Throwable throwable, Object... args) {
        log.error("Redis Fallback 실행. 원인: {}", throwable.getMessage());
        return null;
    }
}
