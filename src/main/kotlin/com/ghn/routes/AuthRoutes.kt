package com.ghn.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ghn.data.requests.CreateAccountRequest
import com.ghn.data.requests.LoginRequest
import com.ghn.data.responses.AuthResponse
import com.ghn.data.responses.BasicApiResponse
import com.ghn.service.UserService
import com.ghn.util.ApiResponseMessages.FIELDS_BLANK
import com.ghn.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.ghn.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.createUser(userService: UserService) {
    post("/user/create") {
        val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (userService.doesUserWithEmailExist(request.email)) {
            call.respond(
                BasicApiResponse<Unit>(
                    successful = false,
                    message = USER_ALREADY_EXISTS
                )
            )
            return@post
        }
        when (userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    BasicApiResponse<Unit>(successful = true)
                )
            }
        }
    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {
    post("user/login") {
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
            return@post
        }

        val isCorrectPassword = userService.isValidPassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )
        if (isCorrectPassword) {
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId", user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = AuthResponse(
                        userId = user.id,
                        token = token
                    )
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
        }
    }
}

fun Route.authenticate() {
    authenticate {
        get("user/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}