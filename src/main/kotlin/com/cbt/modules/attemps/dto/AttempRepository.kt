package com.cbt.modules.attemps

import com.cbt.entities.AttemptEntity
import com.cbt.entities.ExamSessionEntity
import com.cbt.entities.StudentAnswerEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID

class AttemptRepository(private val db: Database) {

    fun findById(id: String): AttemptEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM exam_attempts WHERE id = ?")
            stmt.setObject(1, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toAttemptEntity() else null
        }
    }

    fun findByExamId(idUjian: String): List<AttemptEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM exam_attempts WHERE id_ujian = ?")
            stmt.setObject(1, UUID.fromString(idUjian))
            val rs = stmt.executeQuery()
            val attempts = mutableListOf<AttemptEntity>()
            while (rs.next()) attempts.add(rs.toAttemptEntity())
            attempts
        }
    }

    fun findByUserId(idUser: String): List<AttemptEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM exam_attempts WHERE id_user = ?")
            stmt.setObject(1, UUID.fromString(idUser))
            val rs = stmt.executeQuery()
            val attempts = mutableListOf<AttemptEntity>()
            while (rs.next()) attempts.add(rs.toAttemptEntity())
            attempts
        }
    }

    fun create(attempt: AttemptEntity): AttemptEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                """INSERT INTO exam_attempts (id_ujian, id_user, status, waktu_mulai, waktu_habis, device_info, ip_address, sedang_berlangsung, dikirim)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *"""
            )
            stmt.setObject(1, UUID.fromString(attempt.idUjian))
            stmt.setObject(2, UUID.fromString(attempt.idUser))
            stmt.setString(3, attempt.status)
            stmt.setTimestamp(4, attempt.waktuMulai?.let { Timestamp.valueOf(it) })
            stmt.setTimestamp(5, attempt.waktuHabis?.let { Timestamp.valueOf(it) })
            stmt.setString(6, attempt.deviceInfo)
            stmt.setString(7, attempt.ipAddress)
            stmt.setBoolean(8, attempt.sedangBerlangsung)
            stmt.setBoolean(9, attempt.dikirim)
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toAttemptEntity() else null
        }
    }

    fun updateStatus(id: String, status: String, dikirim: Boolean, waktuKirim: String?): AttemptEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "UPDATE exam_attempts SET status = ?, dikirim = ?, waktu_kirim = ?, sedang_berlangsung = ? WHERE id = ? RETURNING *"
            )
            stmt.setString(1, status)
            stmt.setBoolean(2, dikirim)
            stmt.setTimestamp(3, waktuKirim?.let { Timestamp.valueOf(it) })
            stmt.setBoolean(4, false)
            stmt.setObject(5, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toAttemptEntity() else null
        }
    }

    fun updateScore(id: String, score: Double): AttemptEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "UPDATE exam_attempts SET score = ? WHERE id = ? RETURNING *"
            )
            stmt.setDouble(1, score)
            stmt.setObject(2, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toAttemptEntity() else null
        }
    }

    // JAWABAN SISWA
    fun saveAnswer(answer: StudentAnswerEntity): StudentAnswerEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            // cek apakah sudah ada jawaban untuk soal ini
            val checkStmt = connection.prepareStatement(
                "SELECT id FROM jawaban_siswa WHERE attemp_id = ? AND soal_id = ?"
            )
            checkStmt.setObject(1, UUID.fromString(answer.attempId))
            checkStmt.setObject(2, UUID.fromString(answer.soalId))
            val checkRs = checkStmt.executeQuery()

            if (checkRs.next()) {
                // update jawaban existing
                val updateStmt = connection.prepareStatement(
                    "UPDATE jawaban_siswa SET id_opsi_pilihan = ?, teks_jawaban = ? WHERE attemp_id = ? AND soal_id = ? RETURNING *"
                )
                updateStmt.setObject(1, answer.idOpsiPilihan?.let { UUID.fromString(it) })
                updateStmt.setString(2, answer.teksJawaban)
                updateStmt.setObject(3, UUID.fromString(answer.attempId))
                updateStmt.setObject(4, UUID.fromString(answer.soalId))
                val rs = updateStmt.executeQuery()
                if (rs.next()) rs.toStudentAnswerEntity() else null
            } else {
                // insert jawaban baru
                val insertStmt = connection.prepareStatement(
                    "INSERT INTO jawaban_siswa (attemp_id, soal_id, id_opsi_pilihan, teks_jawaban) VALUES (?, ?, ?, ?) RETURNING *"
                )
                insertStmt.setObject(1, UUID.fromString(answer.attempId))
                insertStmt.setObject(2, UUID.fromString(answer.soalId))
                insertStmt.setObject(3, answer.idOpsiPilihan?.let { UUID.fromString(it) })
                insertStmt.setString(4, answer.teksJawaban)
                val rs = insertStmt.executeQuery()
                if (rs.next()) rs.toStudentAnswerEntity() else null
            }
        }
    }

    fun findAnswersByAttemptId(attempId: String): List<StudentAnswerEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM jawaban_siswa WHERE attemp_id = ?")
            stmt.setObject(1, UUID.fromString(attempId))
            val rs = stmt.executeQuery()
            val answers = mutableListOf<StudentAnswerEntity>()
            while (rs.next()) answers.add(rs.toStudentAnswerEntity())
            answers
        }
    }

    // SESI UJIAN
    fun createSession(session: ExamSessionEntity): ExamSessionEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "INSERT INTO sesi_ujian (id_ujian, id_user, token_ujian) VALUES (?, ?, ?) RETURNING *"
            )
            stmt.setObject(1, UUID.fromString(session.idUjian))
            stmt.setObject(2, UUID.fromString(session.idUser))
            stmt.setString(3, session.tokenUjian)
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toSessionEntity() else null
        }
    }

    fun updatePing(idUser: String, idUjian: String): Boolean {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "UPDATE sesi_ujian SET ping_terakhir = now() WHERE id_user = ? AND id_ujian = ?"
            )
            stmt.setObject(1, UUID.fromString(idUser))
            stmt.setObject(2, UUID.fromString(idUjian))
            stmt.executeUpdate() > 0
        }
    }

    // HITUNG SCORE
    fun calculateScore(attempId: String): Double {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                """SELECT COUNT(*) as total, 
                SUM(CASE WHEN oj.is_correct = true THEN s.poin ELSE 0 END) as total_score,
                SUM(CASE WHEN oj.is_correct = true THEN 1 ELSE 0 END) as benar
                FROM jawaban_siswa js
                JOIN soal s ON js.soal_id = s.id
                LEFT JOIN opsi_jawaban oj ON js.id_opsi_pilihan = oj.id
                WHERE js.attemp_id = ?"""
            )
            stmt.setObject(1, UUID.fromString(attempId))
            val rs = stmt.executeQuery()
            if (rs.next()) rs.getDouble("total_score") else 0.0
        }
    }

    // EXTENSION FUNCTIONS
    private fun java.sql.ResultSet.toAttemptEntity() = AttemptEntity(
        id = getString("id"),
        idUjian = getString("id_ujian"),
        idUser = getString("id_user"),
        status = getString("status"),
        score = getDouble("score"),
        waktuMulai = getString("waktu_mulai"),
        waktuHabis = getString("waktu_habis"),
        waktuKirim = getString("waktu_kirim"),
        deviceInfo = getString("device_info"),
        ipAddress = getString("ip_address"),
        sedangBerlangsung = getBoolean("sedang_berlangsung"),
        dikirim = getBoolean("dikirim")
    )

    private fun java.sql.ResultSet.toStudentAnswerEntity() = StudentAnswerEntity(
        id = getString("id"),
        attempId = getString("attemp_id"),
        soalId = getString("soal_id"),
        idOpsiPilihan = getString("id_opsi_pilihan"),
        teksJawaban = getString("teks_jawaban")
    )

    private fun java.sql.ResultSet.toSessionEntity() = ExamSessionEntity(
        id = getString("id"),
        idUjian = getString("id_ujian"),
        idUser = getString("id_user"),
        waktuMulai = getString("waktu_mulai"),
        pingTerakhir = getString("ping_terakhir"),
        tokenUjian = getString("token_ujian")
    )
}