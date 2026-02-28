package com.cbt.modules.exams.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateExamRequest(
    val judul: String,
    val idMapel: String? = null,
    val durasi: Int,
    val totalSoal: Int,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: String = "draft",
    val soalRandom: Boolean = false,
    val jawabanRandom: Boolean = false
)

@Serializable
data class ExamResponse(
    val id: String,
    val judul: String,
    val idMapel: String?,
    val durasi: Int,
    val totalSoal: Int,
    val startTime: String?,
    val endTime: String?,
    val status: String,
    val soalRandom: Boolean,
    val jawabanRandom: Boolean,
    val createdBy: String?
)