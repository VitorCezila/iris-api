package com.ghn

import io.ktor.server.application.*
import com.ghn.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSecurity()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
}
