package com.cbt.modules.users

import com.cbt.entities.UserEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class UserRepository(private val db: Database) {

    fun findAll(): List<UserEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM users ORDER BY tanggal_dibuat DESC")
            val rs = stmt.executeQuery()
            val users = mutableListOf<UserEntity>()
            while (rs.next()) {
                users.add(
                    UserEntity(
                        id = rs.getString("id"),
                        nama = rs.getString("nama"),
                        nisnNip = rs.getString("nisn_nip"),
                        passwordHash = rs.getString("password_hash"),
                        role = rs.getString("role"),
                        idKelas = rs.getString("id_kelas"),
                        tanggalDibuat = rs.getString("tanggal_dibuat")
                    )
                )
            }
            users
        }
    }

    fun findById(id: String): UserEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
            stmt.setString(1, id)
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

    fun create(nama: String, nisnNip: String, passwordHash: String, role: String, idKelas: String?): UserEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "INSERT INTO users (nama, nisn_nip, password_hash, role, id_kelas) VALUES (?, ?, ?, ?, ?) RETURNING *"
            )
            stmt.setString(1, nama)
            stmt.setString(2, nisnNip)
            stmt.setString(3, passwordHash)
            stmt.setString(4, role)
            stmt.setString(5, idKelas)
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

    fun update(id: String, nama: String, nisnNip: String, role: String, idKelas: String?): UserEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "UPDATE users SET nama = ?, nisn_nip = ?, role = ?, id_kelas = ? WHERE id = ? RETURNING *"
            )
            stmt.setString(1, nama)
            stmt.setString(2, nisnNip)
            stmt.setString(3, role)
            stmt.setString(4, idKelas)
            stmt.setString(5, id)
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

    fun delete(id: String): Boolean {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?")
            stmt.setString(1, id)
            stmt.executeUpdate() > 0
        }
    }
}