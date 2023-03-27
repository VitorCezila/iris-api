package com.ghn.routes

import com.ghn.di.testModule
import com.ghn.service.UserService
import com.google.common.truth.Truth
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


internal class CreateUserRouteTest : KoinTest {

    private val userService: UserService by inject()

    private val gson = Gson()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }


    @Test
    fun `Create user, no body attached, responds with BadRequest`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUser(userService)
                }
                install(ContentNegotiation) {
                    gson()
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/user/create"
            )

            Truth.assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }


}