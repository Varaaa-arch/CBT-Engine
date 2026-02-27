package com.cbt.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {
    fun verify(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }
}