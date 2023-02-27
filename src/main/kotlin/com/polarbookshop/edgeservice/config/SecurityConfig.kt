package com.polarbookshop.edgeservice.config

import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.security.config.*
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.*
import org.springframework.security.oauth2.client.oidc.web.server.logout.*
import org.springframework.security.oauth2.client.registration.*
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.*
import org.springframework.security.web.server.authentication.*
import org.springframework.security.web.server.authentication.logout.*
import org.springframework.security.web.server.csrf.*
import org.springframework.web.server.*
import reactor.core.publisher.*

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
    ): SecurityWebFilterChain =
        http.authorizeExchange { exchange ->
            exchange
                .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                .anyExchange().authenticated()

        }
            .exceptionHandling { eh -> eh.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            .oauth2Login(Customizer.withDefaults())
            .logout { logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)) }
            .csrf { csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()) }
            .build()

    @Bean
    fun csrfWebFilter(): WebFilter = WebFilter { exchange, chain ->
        exchange.response.beforeCommit {
            Mono.defer {
                (exchange.getAttribute(CsrfToken::class.java.name) as Mono<CsrfToken>?)?.then() ?: Mono.empty()
            }
        }
        chain.filter(exchange)
    }


    @Bean
    fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository =
        WebSessionServerOAuth2AuthorizedClientRepository()

    private fun oidcLogoutSuccessHandler(
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
    ): ServerLogoutSuccessHandler {
        val oidcLogoutSuccessHandler = OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcLogoutSuccessHandler
    }
}