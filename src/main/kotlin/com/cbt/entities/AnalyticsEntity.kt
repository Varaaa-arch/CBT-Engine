package com.cbt.entities

data class HasilEntity(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val skor: Double,
    val hitunganBenar: Int,
    val hitunganSalah: Int,
    val durasiMengerjakan: Int
)

data class ExamSummaryEntity(
    val idUjian: String,
    val totalSiswa: Int,
    val rataRata: Double,
    val nilaiTertinggi: Double,
    val nilaiTerendah: Double,
    val totalLulus: Int,
    val totalTidakLulus: Int
)