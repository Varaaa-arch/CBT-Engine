package com.cbt.modules.analytics

import com.cbt.entities.ExamSummaryEntity
import com.cbt.entities.HasilEntity
import com.cbt.modules.analytics.dto.ExamSummaryResponse
import com.cbt.modules.analytics.dto.HasilResponse

class AnalyticsService(private val analyticsRepository: AnalyticsRepository) {

    fun getByExamId(idUjian: String): List<HasilResponse> {
        return analyticsRepository.findByExamId(idUjian).map { it.toResponse() }
    }

    fun getByUserId(idUser: String): List<HasilResponse> {
        return analyticsRepository.findByUserId(idUser).map { it.toResponse() }
    }

    fun getByUserAndExam(idUser: String, idUjian: String): HasilResponse? {
        return analyticsRepository.findByUserAndExam(idUser, idUjian)?.toResponse()
    }

    fun getExamSummary(idUjian: String): ExamSummaryResponse? {
        return analyticsRepository.getExamSummary(idUjian)?.toSummaryResponse()
    }

    fun saveHasil(hasil: HasilEntity): HasilResponse? {
        return analyticsRepository.saveHasil(hasil)?.toResponse()
    }

    private fun HasilEntity.toResponse() = HasilResponse(
        id = id,
        idUjian = idUjian,
        idUser = idUser,
        skor = skor,
        hitunganBenar = hitunganBenar,
        hitunganSalah = hitunganSalah,
        durasiMengerjakan = durasiMengerjakan
    )

    private fun ExamSummaryEntity.toSummaryResponse() = ExamSummaryResponse(
        idUjian = idUjian,
        totalSiswa = totalSiswa,
        rataRata = rataRata,
        nilaiTertinggi = nilaiTertinggi,
        nilaiTerendah = nilaiTerendah,
        totalLulus = totalLulus,
        totalTidakLulus = totalTidakLulus
    )
}