package com.polarbookshop.edgeservice.config

import org.springframework.cloud.gateway.filter.ratelimit.*
import org.springframework.context.annotation.*
import reactor.core.publisher.*

@Configuration
class RateLimiterConfig {
    @Bean
    fun keyResolver(): KeyResolver = KeyResolver { Mono.just("anonymous") }
}