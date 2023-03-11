package com.ghn

import com.ghn.di.mainModule
import com.ghn.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(Koin) {
        this.modules(mainModule)
    }
    configureSecurity()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
}
