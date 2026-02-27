package com.cbt.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cbt.entities.UserEntity
import java.util.Date

object JwtConfig {
    private const val secret = "CBT_SUPER_SECRET"
    private const val issuer = "cbt-app"
    private const val audience = "cbt-users"

    fun generate(user: UserEntity): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id)
            .withClaim("role", user.role)
            .withClaim("nisn_nip", user.nisnNip)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
            .sign(Algorithm.HMAC256(secret))
    }
}