package com.cbt.modules.auth

import com.cbt.entities.UserEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class AuthRepository(private val db: Database) {
    fun findByNisn(nisn: String): UserEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE nisn_nip = ?"
            )

            stmt.setString(1, nisn)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                UserEntity(
                    id = rs.getString("id"),
                    nama = rs.getString("nama"),
                    nisnNip = rs.getString("nisn_nip"),
                    passwordHash = rs.getString("password_hash"),
                    role = rs.getString("role"),
                    idKelas = rs.getString("id_kelas"),
                    tanggalDibuat = rs.getString("tanggal_dibuat")
                )
            } else null
        }
    }
}