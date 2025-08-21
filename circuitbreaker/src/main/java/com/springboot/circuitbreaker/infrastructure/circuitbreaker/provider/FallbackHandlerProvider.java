package com.springboot.circuitbreaker.infrastructure.circuitbreaker.provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.handler.FallbackHandler;

@Component
public class FallbackHandlerProvider {
    private final Map<ProtectionTarget, FallbackHandler> handlers;

    public FallbackHandlerProvider(List<FallbackHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(FallbackHandler::getTarget, handler -> handler));
    }

    public Optional<FallbackHandler> getHandler(ProtectionTarget target) {
        return Optional.ofNullable(handlers.get(target));
    }
}
