package com.cbt.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun ApplicationCall.requireRole(role: String): Boolean {
    val principal = authentication.principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("role")?.asString()

    println("DEBUG - userRole: $userRole, required: $role")

    return if (userRole != role) {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied: required role '$role'"))
        false
    } else true
}