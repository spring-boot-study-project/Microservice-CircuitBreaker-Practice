package com.springboot.circuitbreaker.application.consumer;

import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RegistryEventMonitorConsumer implements RegistryEventConsumer<CircuitBreaker> {

    @Override
    public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        log.info("RegistryEventConsumer.onEntryAddedEvent");

        CircuitBreaker.EventPublisher eventPublisher = entryAddedEvent.getAddedEntry().getEventPublisher();

        eventPublisher.onEvent(event -> log.info("onEvent {}", event));
        eventPublisher.onSuccess(event -> log.info("onSuccess {}", event));
        eventPublisher.onCallNotPermitted(event -> log.info("onCallNotPermitted {}", event));
        eventPublisher.onError(event -> log.info("onError {}", event));
        eventPublisher.onIgnoredError(event -> log.info("onIgnoredError {}", event));
        eventPublisher.onStateTransition(event -> log.info("onStateTransition {}", event));
        eventPublisher.onSlowCallRateExceeded(event -> log.info("onSlowCallRateExceeded {}", event));
        eventPublisher.onFailureRateExceeded(event -> log.info("onFailureRateExceeded {}", event));
    }

    @Override
    public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
        log.info("RegistryEventConsumer.onEntryRemovedEvent");
    }

    @Override
    public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
        log.info("RegistryEventConsumer.onEntryReplacedEvent");
    }
}
