package com.cbt.entities

data class ExamEntity(
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