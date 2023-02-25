package com.polarbookshop.edgeservice.config

import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.reactive.*
import org.springframework.boot.test.mock.mockito.*
import org.springframework.context.annotation.*
import org.springframework.security.oauth2.client.registration.*
import org.springframework.security.oauth2.core.*
import org.springframework.security.test.web.reactive.server.*
import org.springframework.test.web.reactive.server.*
import reactor.core.publisher.*


@WebFluxTest
@Import(SecurityConfig::class)
class SecurityConfigTests(@Autowired val webClient: WebTestClient) {

    @MockBean
    private lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

    @Test
    fun `when logout authenticated and with Csrf Token then 302`() {
        `when`(clientRegistrationRepository.findByRegistrationId("test"))
            .thenReturn(Mono.just(testClientRegistration()))

        webClient.mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/logout")
            .exchange()
            .expectStatus().isFound
    }

    private fun testClientRegistration(): ClientRegistration = ClientRegistration.withRegistrationId("test")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .clientId("test")
        .authorizationUri("https://sso.polarbookshop.com/auth")
        .tokenUri("https://sso.polarbookshop.com/token")
        .redirectUri("https://polarbookshop.com")
        .build()
}