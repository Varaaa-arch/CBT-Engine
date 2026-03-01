package com.cbt.modules.exams

import com.cbt.entities.ExamEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID

class ExamRepository(private val db: Database) {

    fun findAll(): List<ExamEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM ujian ORDER BY start_time DESC")
            val rs = stmt.executeQuery()
            val exams = mutableListOf<ExamEntity>()
            while (rs.next()) {
                exams.add(
                    ExamEntity(
                        id = rs.getString("id"),
                        judul = rs.getString("judul"),
                        idMapel = rs.getString("id_mapel"),
                        durasi = rs.getInt("durasi"),
                        totalSoal = rs.getInt("total_soal"),
                        startTime = rs.getString("start_time"),
                        endTime = rs.getString("end_time"),
                        status = rs.getString("status"),
                        soalRandom = rs.getBoolean("soal_random"),
                        jawabanRandom = rs.getBoolean("jawaban_random"),
                        createdBy = rs.getString("created_by")
                    )
                )
            }
            exams
        }
    }

    fun findById(id: String): ExamEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM ujian WHERE id = ?")
            stmt.setObject(1, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                ExamEntity(
                    id = rs.getString("id"),
                    judul = rs.getString("judul"),
                    idMapel = rs.getString("id_mapel"),
                    durasi = rs.getInt("durasi"),
                    totalSoal = rs.getInt("total_soal"),
                    startTime = rs.getString("start_time"),
                    endTime = rs.getString("end_time"),
                    status = rs.getString("status"),
                    soalRandom = rs.getBoolean("soal_random"),
                    jawabanRandom = rs.getBoolean("jawaban_random"),
                    createdBy = rs.getString("created_by")
                )
            } else null
        }
    }

    fun create(exam: ExamEntity, createdBy: String): ExamEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                """INSERT INTO ujian (judul, id_mapel, durasi, total_soal, start_time, end_time, status, soal_random, jawaban_random, created_by) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *"""
            )
            stmt.setString(1, exam.judul)
            stmt.setObject(2, exam.idMapel?.let { UUID.fromString(it) })
            stmt.setInt(3, exam.durasi)
            stmt.setInt(4, exam.totalSoal)
            stmt.setTimestamp(5, exam.startTime?.let { Timestamp.valueOf(it) })
            stmt.setTimestamp(6, exam.endTime?.let { Timestamp.valueOf(it) })
            stmt.setString(7, exam.status)
            stmt.setBoolean(8, exam.soalRandom)
            stmt.setBoolean(9, exam.jawabanRandom)
            stmt.setObject(10, UUID.fromString(createdBy))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                ExamEntity(
                    id = rs.getString("id"),
                    judul = rs.getString("judul"),
                    idMapel = rs.getString("id_mapel"),
                    durasi = rs.getInt("durasi"),
                    totalSoal = rs.getInt("total_soal"),
                    startTime = rs.getString("start_time"),
                    endTime = rs.getString("end_time"),
                    status = rs.getString("status"),
                    soalRandom = rs.getBoolean("soal_random"),
                    jawabanRandom = rs.getBoolean("jawaban_random"),
                    createdBy = rs.getString("created_by")
                )
            } else null
        }
    }

    fun update(id: String, exam: ExamEntity): ExamEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                """UPDATE ujian SET judul = ?, id_mapel = ?, durasi = ?, total_soal = ?, start_time = ?, 
                end_time = ?, status = ?, soal_random = ?, jawaban_random = ? WHERE id = ? RETURNING *"""
            )
            stmt.setString(1, exam.judul)
            stmt.setObject(2, exam.idMapel?.let { UUID.fromString(it) })
            stmt.setInt(3, exam.durasi)
            stmt.setInt(4, exam.totalSoal)
            stmt.setTimestamp(5, exam.startTime?.let { Timestamp.valueOf(it) })
            stmt.setTimestamp(6, exam.endTime?.let { Timestamp.valueOf(it) })
            stmt.setString(7, exam.status)
            stmt.setBoolean(8, exam.soalRandom)
            stmt.setBoolean(9, exam.jawabanRandom)
            stmt.setObject(10, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                ExamEntity(
                    id = rs.getString("id"),
                    judul = rs.getString("judul"),
                    idMapel = rs.getString("id_mapel"),
                    durasi = rs.getInt("durasi"),
                    totalSoal = rs.getInt("total_soal"),
                    startTime = rs.getString("start_time"),
                    endTime = rs.getString("end_time"),
                    status = rs.getString("status"),
                    soalRandom = rs.getBoolean("soal_random"),
                    jawabanRandom = rs.getBoolean("jawaban_random"),
                    createdBy = rs.getString("created_by")
                )
            } else null
        }
    }

    fun delete(id: String): Boolean {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("DELETE FROM ujian WHERE id = ?")
            stmt.setObject(1, UUID.fromString(id))
            stmt.executeUpdate() > 0
        }
    }
}