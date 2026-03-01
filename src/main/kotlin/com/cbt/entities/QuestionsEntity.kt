package com.cbt.entities

data class QuestionEntity(
    val id: String,
    val idUjian: String,
    val tipeSoal: String,
    val teksSoal: String,
    val image: String?,
    val poin: Int
)

data class AnswerOptionEntity(
    val id: String,
    val idSoal: String,
    val opsiText: String,
    val opsiOrder: Int,
    val isCorrect: Boolean
)