package com.cbt.modules.auth

import com.cbt.entities.UserEntity

class AuthService(private val authRepository: AuthRepository) {
    fun login(nisnNip: String, password: String): Any {
        val user = authRepository.findByNisn(nisnNip)
            ?: return mapOf("error" to "User not found")

        // verify password here (e.g. BCrypt check)
        if (!verifyPassword(password, user.passwordHash)) {
            return mapOf("error" to "Invalid password")
        }

        return mapOf(
            "id" to user.id,
            "nama" to user.nama,
            "role" to user.role
        )
    }

    private fun verifyPassword(plain: String, hash: String): Boolean {
        // replace with BCrypt or your hashing logic
        return plain == hash
    }
}