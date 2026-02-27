package com.example

import com.cbt.modules.auth.authRoutes
import com.cbt.modules.auth.AuthRepository
import com.cbt.modules.auth.AuthService
import com.cbt.config.DatabaseConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val db = DatabaseConfig.init(environment.config)
    val authRepository = AuthRepository(db)
    val authService = AuthService(authRepository)

    routing {
        println("ROUTING KELOAD 🔥")

        get("/") {
            call.respondText("Backend")
        }

        get("/ping") {
            call.respondText("xirpl")
        }

        authRoutes(authService)
    }
}