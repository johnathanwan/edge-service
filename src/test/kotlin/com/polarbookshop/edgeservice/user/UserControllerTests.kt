package com.polarbookshop.edgeservice.user

import com.polarbookshop.edgeservice.config.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTests(@Autowired val webClient: WebTestClient) {

    @MockBean
    private lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

    @Test
    fun `when not authenticated then 401`() {
        webClient.get().uri("/user").exchange().expectStatus().isUnauthorized
    }

    @Test
    fun `when authenticated then return user`() {
        val expectedUser = User("jon.snow", "Jon", "Snow", listOf("employee", "customer"))
        webClient.mutateWith(configureMockOidcLogin(expectedUser))
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(User::class.java)
            .value { user -> assertThat(user).isEqualTo(expectedUser)  }
    }

    private fun configureMockOidcLogin(expectedUser: User): SecurityMockServerConfigurers.OidcLoginMutator =
        SecurityMockServerConfigurers.mockOidcLogin().idToken { builder ->
            builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username)
            builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
            builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
            builder.claim("roles", expectedUser.roles)
        }
}