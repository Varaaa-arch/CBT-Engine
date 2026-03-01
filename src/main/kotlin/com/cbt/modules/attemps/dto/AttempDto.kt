package com.cbt.modules.attemps.dto

import kotlinx.serialization.Serializable

@Serializable
data class StartAttemptRequest(
    val idUjian: String,
    val deviceInfo: String? = null
)

@Serializable
data class AnswerRequest(
    val soalId: String,
    val idOpsiPilihan: String? = null,
    val teksJawaban: String? = null
)

@Serializable
data class StudentAnswerResponse(
    val id: String,
    val attempId: String,
    val soalId: String,
    val idOpsiPilihan: String?,
    val teksJawaban: String?
)

@Serializable
data class AttemptResponse(
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

@Serializable
data class ExamSessionResponse(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val waktuMulai: String?,
    val pingTerakhir: String?,
    val tokenUjian: String
)