package com.cbt.modules.auth.dto

import kotlinx.serialization.Serializable
@Serializable
data class LoginRequest(
    val nisnNip: String,
    val password: String
)