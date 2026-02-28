package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cbt.modules.auth.authRoutes
import com.cbt.modules.auth.AuthRepository
import com.cbt.modules.auth.AuthService
import com.cbt.config.DatabaseConfig
import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256("CBT_SUPER_SECRET"))
                    .withAudience("cbt-users")
                    .withIssuer("cbt-app")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains("cbt-users")) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token invalid or expired"))
            }
        }
    }

    val db = DatabaseConfig.init(environment.config)
    val authRepository = AuthRepository(db)
    val authService = AuthService(authRepository)

    routing {
        println("ROUTING KELOAD 🔥")

        get("/ping") {
            call.respondText("xirpl")
        }

        // PUBLIC
        authRoutes(authService)

        // PROTECTED
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()
                call.respond(mapOf("userId" to userId, "role" to role))
            }

            get("/admin/dashboard") {
                if (!call.requireRole("admin")) return@get
                call.respond(mapOf("message" to "Welcome Admin!"))
            }

            get("/siswa/dashboard") {
                if (!call.requireRole("siswa")) return@get
                call.respond(mapOf("message" to "Welcome Siswa!"))
            }

            get("/guru/dashboard") {
                if (!call.requireRole("guru")) return@get
                call.respond(mapOf("message" to "Welcome Guru!"))
            }

            // nanti tambah routes lain setelah dibuat
            // usersRoutes()
            // examRoutes()
            // questionsRoutes()
            // attemptsRoutes()
            // analyticsRoutes()
        }
    }
}