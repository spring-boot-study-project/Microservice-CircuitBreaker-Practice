package com.springboot.circuitbreaker.infrastructure.circuitbreaker.handler;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

public interface FallbackHandler {
    ProtectionTarget getTarget();

    Object handle(Throwable throwable, Object... args); // 실제 fallback로직입니다.
}
