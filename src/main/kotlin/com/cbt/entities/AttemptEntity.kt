package com.cbt.entities

data class AttemptEntity(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val status: String,
    val score: Double,
    val waktuMulai: String?,
    val waktuHabis: String?,
    val waktuKirim: String?,
    val deviceInfo: String?,
    val ipAddress: String?,
    val sedangBerlangsung: Boolean,
    val dikirim: Boolean
)

data class StudentAnswerEntity(
    val id: String,
    val attempId: String,
    val soalId: String,
    val idOpsiPilihan: String?,
    val teksJawaban: String?
)

data class ExamSessionEntity(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val waktuMulai: String?,
    val pingTerakhir: String?,
    val tokenUjian: String
)