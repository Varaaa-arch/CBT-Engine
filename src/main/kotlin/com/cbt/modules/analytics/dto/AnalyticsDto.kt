package com.cbt.modules.analytics.dto

import kotlinx.serialization.Serializable

@Serializable
data class HasilResponse(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val skor: Double,
    val hitunganBenar: Int,
    val hitunganSalah: Int,
    val durasiMengerjakan: Int
)

@Serializable
data class ExamSummaryResponse(
    val idUjian: String,
    val totalSiswa: Int,
    val rataRata: Double,
    val nilaiTertinggi: Double,
    val nilaiTerendah: Double,
    val totalLulus: Int,
    val totalTidakLulus: Int
)