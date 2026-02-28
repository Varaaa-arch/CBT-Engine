package com.cbt.entities

data class UserEntity(
    val id: String,
    val nama: String,
    val nisnNip: String,
    val passwordHash: String,
    val role: String,
    val idKelas: String?,
    val tanggalDibuat: String?
)