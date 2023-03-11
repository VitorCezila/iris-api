package com.ghn.plugins

import com.ghn.routes.authenticate
import com.ghn.routes.createUser
import com.ghn.routes.loginUser
import com.ghn.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()

    routing {
        // User routes
        authenticate()
        createUser(userService = userService)
    }
}
