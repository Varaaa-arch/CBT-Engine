package com.cbt.modules.auth

import com.cbt.utils.PasswordUtil

class AuthService(private val authRepository: AuthRepository) {
    fun login(nisnNip: String, password: String): Any {
        val user = authRepository.findByNisn(nisnNip)
            ?: return mapOf("error" to "User not found")

        if (!PasswordUtil.verify(password, user.passwordHash)) {
            return mapOf("error" to "Invalid password")
        }

        return mapOf(
            "id" to user.id,
            "nama" to user.nama,
            "role" to user.role
        )
    }
}