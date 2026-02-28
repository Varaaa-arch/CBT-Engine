package com.cbt.modules.exams

import com.cbt.modules.exams.dto.CreateExamRequest
import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.examRoutes(examService: ExamService) {
    route("/exams") {
        get {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }
            val exams = examService.getAll()
            call.respond(exams)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val exam = examService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Exam not found"))
            call.respond(exam)
        }

        post {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@post
            }
            val principal = call.authentication.principal<JWTPrincipal>()
            val createdBy = principal?.payload?.getClaim("userId")?.asString() ?: return@post
            val request = call.receive<CreateExamRequest>()
            val exam = examService.create(request, createdBy)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create exam"))
            call.respond(HttpStatusCode.Created, exam)
        }

        put("/{id}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@put
            }
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<CreateExamRequest>()
            val exam = examService.update(id, request)
                ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Exam not found"))
            call.respond(exam)
        }

        delete("/{id}") {
            if (!call.requireRole("admin")) return@delete
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = examService.delete(id)
            if (deleted) {
                call.respond(mapOf("message" to "Exam deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Exam not found"))
            }
        }
    }
}