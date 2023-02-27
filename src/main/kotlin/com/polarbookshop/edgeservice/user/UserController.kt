package com.polarbookshop.edgeservice.user

import org.springframework.security.core.*
import org.springframework.security.core.annotation.*
import org.springframework.security.oauth2.core.oidc.user.*
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.*

@RestController
class UserController {

    @GetMapping("user")
    fun getUser(@AuthenticationPrincipal oidcUser: OidcUser): Mono<User> {
        val user =
            User(
                oidcUser.preferredUsername,
                oidcUser.givenName,
                oidcUser.familyName,
                oidcUser.getClaimAsStringList("roles")
            )
        return Mono.just(user)
    }
}