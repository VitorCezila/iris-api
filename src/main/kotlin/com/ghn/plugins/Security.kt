package com.ghn.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*

fun Application.configureSecurity() {
    
    authentication {
            jwt {
                val jwtAudience = System.getenv("JWT_AUDIENCE")
                realm = System.getenv("JWT_REALM")
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(System.getenv("JWT_SECRET")))
                        .withAudience(jwtAudience)
                        .withIssuer(System.getenv("JWT_DOMAIN"))
                        .build()
                )
                validate { credential ->
                    if (credential.payload.audience.contains(jwtAudience)) {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
        }
}

val JWTPrincipal.userId: String?
    get() = getClaim("userId", String::class)