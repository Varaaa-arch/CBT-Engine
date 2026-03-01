package com.cbt.modules.attemps

import com.cbt.entities.AttemptEntity
import com.cbt.entities.ExamSessionEntity
import com.cbt.entities.HasilEntity
import com.cbt.entities.StudentAnswerEntity
import com.cbt.modules.analytics.AnalyticsRepository
import com.cbt.modules.attemps.dto.AnswerRequest
import com.cbt.modules.attemps.dto.AttemptResponse
import com.cbt.modules.attemps.dto.ExamSessionResponse
import com.cbt.modules.attemps.dto.StartAttemptRequest
import com.cbt.modules.attemps.dto.StudentAnswerResponse
import java.time.LocalDateTime
import java.util.UUID

class AttemptService(
    private val attemptRepository: AttemptRepository,
    private val analyticsRepository: AnalyticsRepository
) {

    fun startAttempt(request: StartAttemptRequest, idUser: String, ipAddress: String?): AttemptResponse? {
        val now = LocalDateTime.now().toString().replace("T", " ").substring(0, 19)

        val attempt = AttemptEntity(
            id = "",
            idUjian = request.idUjian,
            idUser = idUser,
            status = "berlangsung",
            score = 0.0,
            waktuMulai = now,
            waktuHabis = null,
            waktuKirim = null,
            deviceInfo = request.deviceInfo,
            ipAddress = ipAddress,
            sedangBerlangsung = true,
            dikirim = false
        )

        val created = attemptRepository.create(attempt) ?: return null

        // buat sesi ujian
        val token = UUID.randomUUID().toString()
        attemptRepository.createSession(
            ExamSessionEntity(
                id = "",
                idUjian = request.idUjian,
                idUser = idUser,
                waktuMulai = now,
                pingTerakhir = now,
                tokenUjian = token
            )
        )

        return created.toResponse()
    }

    fun saveAnswer(attempId: String, request: AnswerRequest): StudentAnswerResponse? {
        val answer = StudentAnswerEntity(
            id = "",
            attempId = attempId,
            soalId = request.soalId,
            idOpsiPilihan = request.idOpsiPilihan,
            teksJawaban = request.teksJawaban
        )
        return attemptRepository.saveAnswer(answer)?.toResponse()
    }

    fun submitAttempt(attempId: String): AttemptResponse? {
        val now = LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
        val attempt = attemptRepository.findById(attempId) ?: return null

        // hitung score
        val score = attemptRepository.calculateScore(attempId)
        val answers = attemptRepository.findAnswersByAttemptId(attempId)

        // hitung benar salah
        val hitunganBenar = answers.count { it.idOpsiPilihan != null }
        val hitunganSalah = answers.count { it.idOpsiPilihan == null }

        // update score
        attemptRepository.updateScore(attempId, score)

        // simpan ke tabel hasil
        analyticsRepository.saveHasil(
            HasilEntity(
                id = "",
                idUjian = attempt.idUjian,
                idUser = attempt.idUser,
                skor = score,
                hitunganBenar = hitunganBenar,
                hitunganSalah = hitunganSalah,
                durasiMengerjakan = 0
            )
        )

        return attemptRepository.updateStatus(
            id = attempId,
            status = "selesai",
            dikirim = true,
            waktuKirim = now
        )?.toResponse()
    }

    fun getById(id: String): AttemptResponse? {
        return attemptRepository.findById(id)?.toResponse()
    }

    fun getByExamId(idUjian: String): List<AttemptResponse> {
        return attemptRepository.findByExamId(idUjian).map { it.toResponse() }
    }

    fun getByUserId(idUser: String): List<AttemptResponse> {
        return attemptRepository.findByUserId(idUser).map { it.toResponse() }
    }

    fun getAnswers(attempId: String): List<StudentAnswerResponse> {
        return attemptRepository.findAnswersByAttemptId(attempId).map { it.toResponse() }
    }

    fun ping(idUser: String, idUjian: String): Boolean {
        return attemptRepository.updatePing(idUser, idUjian)
    }

    private fun AttemptEntity.toResponse() = AttemptResponse(
        id = id,
        idUjian = idUjian,
        idUser = idUser,
        status = status,
        score = score,
        waktuMulai = waktuMulai,
        waktuHabis = waktuHabis,
        waktuKirim = waktuKirim,
        deviceInfo = deviceInfo,
        ipAddress = ipAddress,
        sedangBerlangsung = sedangBerlangsung,
        dikirim = dikirim
    )

    private fun StudentAnswerEntity.toResponse() = StudentAnswerResponse(
        id = id,
        attempId = attempId,
        soalId = soalId,
        idOpsiPilihan = idOpsiPilihan,
        teksJawaban = teksJawaban
    )
}