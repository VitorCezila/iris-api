package com.ghn.routes

import com.ghn.plugins.userId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()