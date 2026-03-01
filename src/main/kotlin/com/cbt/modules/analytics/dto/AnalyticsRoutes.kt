package com.cbt.modules.analytics

import com.cbt.utils.requireRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.analyticsRoutes(analyticsService: AnalyticsService) {
    route("/analytics") {

        // hasil semua siswa per ujian
        get("/exam/{idUjian}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru" && role != "pengawas") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }
            val idUjian = call.parameters["idUjian"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val hasil = analyticsService.getByExamId(idUjian)
            call.respond(hasil)
        }

        // ringkasan ujian
        get("/exam/{idUjian}/summary") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            if (role != "admin" && role != "guru") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }
            val idUjian = call.parameters["idUjian"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val summary = analyticsService.getExamSummary(idUjian)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "No data found"))
            call.respond(summary)
        }

        // hasil semua ujian per siswa
        get("/user/{idUser}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            val principal = call.authentication.principal<JWTPrincipal>()
            val myId = principal?.payload?.getClaim("userId")?.asString()
            val idUser = call.parameters["idUser"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            // siswa hanya bisa lihat hasil sendiri
            if (role == "siswa" && myId != idUser) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }

            val hasil = analyticsService.getByUserId(idUser)
            call.respond(hasil)
        }

        // detail hasil siswa per ujian
        get("/user/{idUser}/exam/{idUjian}") {
            val role = call.authentication.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
            val principal = call.authentication.principal<JWTPrincipal>()
            val myId = principal?.payload?.getClaim("userId")?.asString()
            val idUser = call.parameters["idUser"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val idUjian = call.parameters["idUjian"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            // siswa hanya bisa lihat hasil sendiri
            if (role == "siswa" && myId != idUser) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                return@get
            }

            val hasil = analyticsService.getByUserAndExam(idUser, idUjian)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "No data found"))
            call.respond(hasil)
        }
    }
}