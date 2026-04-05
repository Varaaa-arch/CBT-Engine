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

        // --- ENDPOINT BARU UNTUK ANDROID (Siswa) ---
        // Digunakan di DetailUjianActivity untuk validasi token soal
        get("/check-token/{token}") {
            val token = call.parameters["token"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Token is required"))

            // Kita asumsikan di ExamService lo buat fungsi 'getByToken'
            val exam = examService.getByToken(token)
            if (exam != null) {
                call.respond(exam)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Token ujian tidak valid atau tidak ditemukan"))
            }
        }

        // --- ENDPOINT EXIST YANG SUDAH ADA ---
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