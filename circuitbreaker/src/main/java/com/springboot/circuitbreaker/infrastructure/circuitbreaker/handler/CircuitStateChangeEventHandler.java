package com.springboot.circuitbreaker.infrastructure.circuitbreaker.handler;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitStateChangeEventHandler {
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final KafkaListenerEndpointRegistry kafkaRegistry;

    // Consumer ID와 이 Consumer가 사용하는 서킷 브레이커 타겟을 매핑
    // 실제 프로젝트에서는 이 정보를 외부 설정으로 관리할 수 있습니다.
    private static final String DB_PROCESSING_CONSUMER_ID = "order-creation-consumer";
    private static final ProtectionTarget DB_CIRCUIT_TARGET = ProtectionTarget.DB;

    @PostConstruct
    public void registerEventListener() {
        // DB 서킷 브레이커 인스턴스를 가져온다.
        CircuitBreaker dbCircuit = circuitBreakerRegistry.circuitBreaker(DB_CIRCUIT_TARGET.getCircuitBreakerName());

        // 상태 변경 이벤트를 구독한다.
        dbCircuit.getEventPublisher()
                .onStateTransition(event -> {
                    log.info("DB 서킷 브레이커 상태 변경 감지: {}", event);

                    // 시스템이 완전히 회복되었다고 확신할 수 있는 시점은 HALF_OPEN -> CLOSED 전환 시점이다.
                    if (event.getStateTransition() == CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED) {
                        resumeConsumer(DB_PROCESSING_CONSUMER_ID);
                    }
                });
    }

    private void resumeConsumer(String consumerId) {
        MessageListenerContainer listenerContainer = kafkaRegistry.getListenerContainer(consumerId);
        if (listenerContainer != null && listenerContainer.isContainerPaused()) {
            log.warn("[AUTO-RECOVERY] 서킷 브레이커가 회복되어 중단된 컨슈머를 재개합니다. ID: {}", consumerId);
            listenerContainer.resume();
        }
    }
}
