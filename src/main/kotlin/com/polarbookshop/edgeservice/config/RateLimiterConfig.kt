package com.polarbookshop.edgeservice.config

import org.springframework.cloud.gateway.filter.ratelimit.*
import org.springframework.context.annotation.*
import java.security.*

@Configuration
class RateLimiterConfig {
    @Bean
    fun keyResolver(): KeyResolver = KeyResolver {
        it.getPrincipal<Principal>().map(Principal::getName).defaultIfEmpty("anonymous")
    }
}