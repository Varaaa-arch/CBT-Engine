package com.cbt.modules.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val nama: String,
    val nisnNip: String,
    val password: String,
    val role: String,
    val idKelas: String? = null
)

@Serializable
data class UserResponse(
    val id: String,
    val nama: String,
    val nisnNip: String,
    val role: String,
    val idKelas: String?,
    val tanggalDibuat: String?
)