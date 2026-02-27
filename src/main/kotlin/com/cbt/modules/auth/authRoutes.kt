package com.cbt.modules.auth

import com.cbt.modules.auth.dto.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val result: Any = authService.login(
                request.nisnNip,
                request.password
            )
            call.respond(HttpStatusCode.OK, result)
        }
    }
}