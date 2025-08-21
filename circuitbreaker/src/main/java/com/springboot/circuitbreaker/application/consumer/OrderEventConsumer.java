package com.springboot.circuitbreaker.application.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import com.springboot.circuitbreaker.domain.enums.ProtectionTarget;
import com.springboot.circuitbreaker.domain.order.Order;
import com.springboot.circuitbreaker.infrastructure.circuitbreaker.executor.CircuitExecutor;
import com.springboot.circuitbreaker.infrastructure.messaging.producer.OrderEventProducer;
import com.springboot.circuitbreaker.infrastructure.persistence.cache.OrderCacheRepository;
import com.springboot.circuitbreaker.infrastructure.persistence.db.OrderServiceRepository;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final CircuitExecutor circuitExecutor;
    private final OrderServiceRepository dbRepository;
    private final OrderCacheRepository cacheRepository;
    private final OrderEventProducer orderEventProducer;
    private final KafkaListenerEndpointRegistry kafkaRegistry;

    private static final String CONSUMER_ID = "order-creation-consumer";

    @KafkaListener(id = CONSUMER_ID, topics = "order-creation-topic")
    public void handleOrderCreation(Order order) {
        log.info("신규 주문 수신: {}", order.getOrderId());

        // 1. DB 저장 (프로그래밍 방식 서킷/재시도, Fallback으로 DLQ 전송)
        Try<Void> originalTry = circuitExecutor.execute(ProtectionTarget.DB, () -> {
            dbRepository.save(order);
            return null; // Supplier<Void>
        });

        originalTry.onFailure(throwable -> {
            // 2-1. 시스템 전체 장애 대응 (서킷 OPEN)
            if (throwable instanceof CallNotPermittedException) {
                log.error("[CRITICAL] 서킷 브레이커가 OPEN 되었습니다. 컨슈머를 일시 중지합니다. ID: {}", CONSUMER_ID, throwable);
                kafkaRegistry.getListenerContainer(CONSUMER_ID).pause();
            } else {
                log.error("DB 저장 개별 실패. DLQ로 메시지 전송. orderId: {}", order.getOrderId(), throwable);
                orderEventProducer.sendOrderToDlq(order);
            }
        });

        Try<Void> originalCacheTry = circuitExecutor.execute(ProtectionTarget.REDIS, () -> {
            String key = order.getOrderId().toString() + "_orderId";
            cacheRepository.saveAndCache(key, order);
            return null; // Supplier<Void>
        });

        originalCacheTry.onFailure(throwable -> {
            // 2-1. 시스템 전체 장애 대응 (서킷 OPEN)
            if (throwable instanceof CallNotPermittedException) {
                log.error("[CRITICAL] 서킷 브레이커가 OPEN 되었습니다. 컨슈머를 일시 중지합니다. ID: {}", CONSUMER_ID, throwable);
                kafkaRegistry.getListenerContainer(CONSUMER_ID).pause();
            } else {
                log.error("Redis 저장 개별 실패. DLQ로 메시지 전송. orderId: {}", order.getOrderId(), throwable);
                orderEventProducer.sendOrderToDlq(order);
            }
        });
    }
}
