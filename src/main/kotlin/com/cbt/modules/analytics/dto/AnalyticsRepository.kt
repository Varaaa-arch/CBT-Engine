package com.cbt.modules.analytics

import com.cbt.entities.ExamSummaryEntity
import com.cbt.entities.HasilEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.util.UUID

class AnalyticsRepository(private val db: Database) {

    fun findByExamId(idUjian: String): List<HasilEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM hasil WHERE id_ujian = ?")
            stmt.setObject(1, UUID.fromString(idUjian))
            val rs = stmt.executeQuery()
            val hasil = mutableListOf<HasilEntity>()
            while (rs.next()) {
                hasil.add(rs.toHasilEntity())
            }
            hasil
        }
    }

    fun findByUserId(idUser: String): List<HasilEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM hasil WHERE id_user = ?")
            stmt.setObject(1, UUID.fromString(idUser))
            val rs = stmt.executeQuery()
            val hasil = mutableListOf<HasilEntity>()
            while (rs.next()) {
                hasil.add(rs.toHasilEntity())
            }
            hasil
        }
    }

    fun findByUserAndExam(idUser: String, idUjian: String): HasilEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "SELECT * FROM hasil WHERE id_user = ? AND id_ujian = ?"
            )
            stmt.setObject(1, UUID.fromString(idUser))
            stmt.setObject(2, UUID.fromString(idUjian))
            val rs = stmt.executeQuery()
            if (rs.next()) rs.toHasilEntity() else null
        }
    }

    fun getExamSummary(idUjian: String): ExamSummaryEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                """SELECT 
                    COUNT(*) as total_siswa,
                    AVG(skor) as rata_rata,
                    MAX(skor) as nilai_tertinggi,
                    MIN(skor) as nilai_terendah,
                    SUM(CASE WHEN skor >= 70 THEN 1 ELSE 0 END) as total_lulus,
                    SUM(CASE WHEN skor < 70 THEN 1 ELSE 0 END) as total_tidak_lulus
                FROM hasil WHERE id_ujian = ?"""
            )
            stmt.setObject(1, UUID.fromString(idUjian))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                ExamSummaryEntity(
                    idUjian = idUjian,
                    totalSiswa = rs.getInt("total_siswa"),
                    rataRata = rs.getDouble("rata_rata"),
                    nilaiTertinggi = rs.getDouble("nilai_tertinggi"),
                    nilaiTerendah = rs.getDouble("nilai_terendah"),
                    totalLulus = rs.getInt("total_lulus"),
                    totalTidakLulus = rs.getInt("total_tidak_lulus")
                )
            } else null
        }
    }

    fun saveHasil(hasil: HasilEntity): HasilEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            // cek apakah sudah ada hasil untuk user dan ujian ini
            val checkStmt = connection.prepareStatement(
                "SELECT id FROM hasil WHERE id_user = ? AND id_ujian = ?"
            )
            checkStmt.setObject(1, UUID.fromString(hasil.idUser))
            checkStmt.setObject(2, UUID.fromString(hasil.idUjian))
            val checkRs = checkStmt.executeQuery()

            if (checkRs.next()) {
                // update
                val updateStmt = connection.prepareStatement(
                    """UPDATE hasil SET skor = ?, hitungan_benar = ?, hitungan_salah = ?, 
                    durasi_mengerjakan = ? WHERE id_user = ? AND id_ujian = ? RETURNING *"""
                )
                updateStmt.setDouble(1, hasil.skor)
                updateStmt.setInt(2, hasil.hitunganBenar)
                updateStmt.setInt(3, hasil.hitunganSalah)
                updateStmt.setInt(4, hasil.durasiMengerjakan)
                updateStmt.setObject(5, UUID.fromString(hasil.idUser))
                updateStmt.setObject(6, UUID.fromString(hasil.idUjian))
                val rs = updateStmt.executeQuery()
                if (rs.next()) rs.toHasilEntity() else null
            } else {
                // insert
                val insertStmt = connection.prepareStatement(
                    """INSERT INTO hasil (id_ujian, id_user, skor, hitungan_benar, hitungan_salah, durasi_mengerjakan)
                    VALUES (?, ?, ?, ?, ?, ?) RETURNING *"""
                )
                insertStmt.setObject(1, UUID.fromString(hasil.idUjian))
                insertStmt.setObject(2, UUID.fromString(hasil.idUser))
                insertStmt.setDouble(3, hasil.skor)
                insertStmt.setInt(4, hasil.hitunganBenar)
                insertStmt.setInt(5, hasil.hitunganSalah)
                insertStmt.setInt(6, hasil.durasiMengerjakan)
                val rs = insertStmt.executeQuery()
                if (rs.next()) rs.toHasilEntity() else null
            }
        }
    }

    private fun java.sql.ResultSet.toHasilEntity() = HasilEntity(
        id = getString("id"),
        idUjian = getString("id_ujian"),
        idUser = getString("id_user"),
        skor = getDouble("skor"),
        hitunganBenar = getInt("hitungan_benar"),
        hitunganSalah = getInt("hitungan_salah"),
        durasiMengerjakan = getInt("durasi_mengerjakan")
    )
}