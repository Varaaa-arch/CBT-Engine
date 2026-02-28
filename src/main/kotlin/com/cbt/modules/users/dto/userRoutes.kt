package com.cbt.modules.users

import com.cbt.modules.users.dto.CreateUserRequest
import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoutes(userService: UserService) {
    route("/users") {
        get {
            if (!call.requireRole("admin")) return@get
            val users = userService.getAll()
            call.respond(users)
        }

        get("/{id}") {
            if (!call.requireRole("admin")) return@get
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val user = userService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            call.respond(user)
        }

        post {
            if (!call.requireRole("admin")) return@post
            val request = call.receive<CreateUserRequest>()
            val user = userService.create(request)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create user"))
            call.respond(HttpStatusCode.Created, user)
        }

        put("/{id}") {
            if (!call.requireRole("admin")) return@put
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<CreateUserRequest>()
            val user = userService.update(id, request)
                ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            call.respond(user)
        }

        delete("/{id}") {
            if (!call.requireRole("admin")) return@delete
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = userService.delete(id)
            if (deleted) {
                call.respond(mapOf("message" to "User deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
        }
    }
}