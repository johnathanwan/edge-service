package com.polarbookshop.edgeservice

import org.springframework.boot.test.context.*
import org.springframework.test.context.*
import org.testcontainers.containers.*
import org.testcontainers.junit.jupiter.*
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EdgeServiceApplicationTests {

    companion object {
        @JvmStatic
        private val REDIS_PORT = 6379

        @Container
        val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.0.8")).withExposedPorts(REDIS_PORT)

        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host") { redis.host }
            registry.add("spring.redis.port") { redis.getMappedPort(REDIS_PORT)}
        }
    }

}
