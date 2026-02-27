package com.cbt.modules.plugins

import com.cbt.modules.auth.AuthRepository
import com.cbt.modules.auth.AuthService
import com.cbt.modules.auth.authRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.configureRouting(db: Database) {
    val authRepository = AuthRepository(db)
    val authService = AuthService(authRepository)

    routing {
        authRoutes(authService)
    }
}