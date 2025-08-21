package com.springboot.circuitbreaker.domain.enums;

public enum ProtectionTarget {
    DB("db-circuit", "db-retry"),
    REDIS("redis-circuit", "redis-retry"),

    KAFKA_PRODUCER("kafka-producer-circuit", "kafka-producer-retry"),
    RESTTEMPLATE("resttemplate-circuit", "resttemplate-retry"),

    FEIGN("feign-circuit", "feign-retry")

    ;

    // 외부 API 등 필요에 따라 추가

    private final String circuitBreakerName;
    private final String retryName;

    ProtectionTarget(String circuitBreakerName, String retryName) {
        this.circuitBreakerName = circuitBreakerName;
        this.retryName = retryName;
    }

    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }

    public String getRetryName() {
        return retryName;
    }

    public static class Constants {
        public static final String DB_RETRY = "db-retry";
        public static final String REDIS_RETRY = "redis-retry";

    }
}
