package com.cbt.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun ApplicationCall.requireRole(vararg roles: String): Boolean {
    val principal = authentication.principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("role")?.asString()

    return if (userRole !in roles) {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
        false
    } else true
}