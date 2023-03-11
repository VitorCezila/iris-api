package com.ghn.plugins

import com.ghn.routes.createUser
import com.ghn.routes.followUser
import com.ghn.routes.loginUser
import com.ghn.service.FollowService
import com.ghn.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()

    routing {
        // User routes
        createUser(userService = userService)
        loginUser(userService = userService)

        // Following routes
        followUser(followService = followService)
    }
}
