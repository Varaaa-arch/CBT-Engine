package com.cbt.modules.questions

import com.cbt.modules.questions.dto.CreateQuestionRequest
import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.questionRoutes(questionService: QuestionService) {
    route("/questions") {
        get {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }
            val exams = questionService.getAll()
            call.respond(exams)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val question = questionService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Question not found"))
            call.respond(question)
        }

        get("/exam/{idUjian}") {
            val idUjian = call.parameters["idUjian"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val questions = questionService.getByExamId(idUjian)
            call.respond(questions)
        }

        post {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@post
            }
            val request = call.receive<CreateQuestionRequest>()
            val question = questionService.create(request)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create question"))
            call.respond(HttpStatusCode.Created, question)
        }

        put("/{id}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@put
            }
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<CreateQuestionRequest>()
            val question = questionService.update(id, request)
                ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Question not found"))
            call.respond(question)
        }

        delete("/{id}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@delete
            }
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = questionService.delete(id)
            if (deleted) {
                call.respond(mapOf("message" to "Question deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Question not found"))
            }
        }
    }
}