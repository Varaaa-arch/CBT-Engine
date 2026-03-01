package com.cbt.modules.questions.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateAnswerOptionRequest(
    val opsiText: String,
    val opsiOrder: Int,
    val isCorrect: Boolean
)

@Serializable
data class CreateQuestionRequest(
    val idUjian: String,
    val tipeSoal: String,
    val teksSoal: String,
    val image: String? = null,
    val poin: Int,
    val opsiJawaban: List<CreateAnswerOptionRequest> = emptyList()
)

@Serializable
data class AnswerOptionResponse(
    val id: String,
    val idSoal: String,
    val opsiText: String,
    val opsiOrder: Int,
    val isCorrect: Boolean
)

@Serializable
data class QuestionResponse(
    val id: String,
    val idUjian: String,
    val tipeSoal: String,
    val teksSoal: String,
    val image: String?,
    val poin: Int,
    val opsiJawaban: List<AnswerOptionResponse> = emptyList()
)