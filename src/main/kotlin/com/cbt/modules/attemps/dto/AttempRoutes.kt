package com.cbt.modules.attemps

import com.cbt.modules.attemps.dto.AnswerRequest
import com.cbt.modules.attemps.dto.StartAttemptRequest
import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.attemptRoutes(attemptService: AttemptService) {
    route("/attempts") {

        // siswa mulai ujian
        post("/start") {
            if (!call.requireRole("siswa")) return@post
            val principal = call.authentication.principal<JWTPrincipal>()
            val idUser = principal?.payload?.getClaim("userId")?.asString() ?: return@post
            val ipAddress = call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteAddress
            val request = call.receive<StartAttemptRequest>()
            val attempt = attemptService.startAttempt(request, idUser, ipAddress)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to start attempt"))
            call.respond(HttpStatusCode.Created, attempt)
        }

        // siswa jawab soal
        post("/{id}/answer") {
            if (!call.requireRole("siswa")) return@post
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<AnswerRequest>()
            val answer = attemptService.saveAnswer(id, request)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to save answer"))
            call.respond(answer)
        }

        // siswa submit ujian
        post("/{id}/submit") {
            if (!call.requireRole("siswa")) return@post
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val attempt = attemptService.submitAttempt(id)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to submit attempt"))
            call.respond(attempt)
        }

        // detail attempt
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val attempt = attemptService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Attempt not found"))
            call.respond(attempt)
        }

        // list attempt per ujian (pengawas, admin)
        get("/exam/{idUjian}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "pengawas") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }
            val idUjian = call.parameters["idUjian"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val attempts = attemptService.getByExamId(idUjian)
            call.respond(attempts)
        }

        // list attempt milik siswa
        get("/my") {
            if (!call.requireRole("siswa")) return@get
            val principal = call.authentication.principal<JWTPrincipal>()
            val idUser = principal?.payload?.getClaim("userId")?.asString() ?: return@get
            val attempts = attemptService.getByUserId(idUser)
            call.respond(attempts)
        }

        // jawaban siswa per attempt
        get("/{id}/answers") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val answers = attemptService.getAnswers(id)
            call.respond(answers)
        }

        // ping - update kehadiran siswa
        post("/{idUjian}/ping") {
            if (!call.requireRole("siswa")) return@post
            val principal = call.authentication.principal<JWTPrincipal>()
            val idUser = principal?.payload?.getClaim("userId")?.asString() ?: return@post
            val idUjian = call.parameters["idUjian"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            attemptService.ping(idUser, idUjian)
            call.respond(mapOf("message" to "Ping received"))
        }
    }
}