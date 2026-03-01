package com.cbt.modules.questions

import com.cbt.entities.AnswerOptionEntity
import com.cbt.entities.QuestionEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.util.UUID

class QuestionRepository(private val db: Database) {

    fun findAll(): List<QuestionEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM soal")
            val rs = stmt.executeQuery()
            val questions = mutableListOf<QuestionEntity>()
            while (rs.next()) {
                questions.add(
                    QuestionEntity(
                        id = rs.getString("id"),
                        idUjian = rs.getString("id_ujian"),
                        tipeSoal = rs.getString("tipe_soal"),
                        teksSoal = rs.getString("teks_soal"),
                        image = rs.getString("image"),
                        poin = rs.getInt("poin")
                    )
                )
            }
            questions
        }
    }

    fun findById(id: String): QuestionEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM soal WHERE id = ?")
            stmt.setObject(1, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                QuestionEntity(
                    id = rs.getString("id"),
                    idUjian = rs.getString("id_ujian"),
                    tipeSoal = rs.getString("tipe_soal"),
                    teksSoal = rs.getString("teks_soal"),
                    image = rs.getString("image"),
                    poin = rs.getInt("poin")
                )
            } else null
        }
    }

    fun findByExamId(idUjian: String): List<QuestionEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM soal WHERE id_ujian = ?")
            stmt.setObject(1, UUID.fromString(idUjian))
            val rs = stmt.executeQuery()
            val questions = mutableListOf<QuestionEntity>()
            while (rs.next()) {
                questions.add(
                    QuestionEntity(
                        id = rs.getString("id"),
                        idUjian = rs.getString("id_ujian"),
                        tipeSoal = rs.getString("tipe_soal"),
                        teksSoal = rs.getString("teks_soal"),
                        image = rs.getString("image"),
                        poin = rs.getInt("poin")
                    )
                )
            }
            questions
        }
    }

    fun create(question: QuestionEntity): QuestionEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "INSERT INTO soal (id_ujian, tipe_soal, teks_soal, image, poin) VALUES (?, ?, ?, ?, ?) RETURNING *"
            )
            stmt.setObject(1, UUID.fromString(question.idUjian))
            stmt.setString(2, question.tipeSoal)
            stmt.setString(3, question.teksSoal)
            stmt.setString(4, question.image)
            stmt.setInt(5, question.poin)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                QuestionEntity(
                    id = rs.getString("id"),
                    idUjian = rs.getString("id_ujian"),
                    tipeSoal = rs.getString("tipe_soal"),
                    teksSoal = rs.getString("teks_soal"),
                    image = rs.getString("image"),
                    poin = rs.getInt("poin")
                )
            } else null
        }
    }

    fun update(id: String, question: QuestionEntity): QuestionEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "UPDATE soal SET tipe_soal = ?, teks_soal = ?, image = ?, poin = ? WHERE id = ? RETURNING *"
            )
            stmt.setString(1, question.tipeSoal)
            stmt.setString(2, question.teksSoal)
            stmt.setString(3, question.image)
            stmt.setInt(4, question.poin)
            stmt.setObject(5, UUID.fromString(id))
            val rs = stmt.executeQuery()
            if (rs.next()) {
                QuestionEntity(
                    id = rs.getString("id"),
                    idUjian = rs.getString("id_ujian"),
                    tipeSoal = rs.getString("tipe_soal"),
                    teksSoal = rs.getString("teks_soal"),
                    image = rs.getString("image"),
                    poin = rs.getInt("poin")
                )
            } else null
        }
    }

    fun delete(id: String): Boolean {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("DELETE FROM soal WHERE id = ?")
            stmt.setObject(1, UUID.fromString(id))
            stmt.executeUpdate() > 0
        }
    }

    // OPSI JAWABAN
    fun findOptionsByQuestionId(idSoal: String): List<AnswerOptionEntity> {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("SELECT * FROM opsi_jawaban WHERE id_soal = ? ORDER BY opsi_order")
            stmt.setObject(1, UUID.fromString(idSoal))
            val rs = stmt.executeQuery()
            val options = mutableListOf<AnswerOptionEntity>()
            while (rs.next()) {
                options.add(
                    AnswerOptionEntity(
                        id = rs.getString("id"),
                        idSoal = rs.getString("id_soal"),
                        opsiText = rs.getString("opsi_text"),
                        opsiOrder = rs.getInt("opsi_order"),
                        isCorrect = rs.getBoolean("is_correct")
                    )
                )
            }
            options
        }
    }

    fun createOption(option: AnswerOptionEntity): AnswerOptionEntity? {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement(
                "INSERT INTO opsi_jawaban (id_soal, opsi_text, opsi_order, is_correct) VALUES (?, ?, ?, ?) RETURNING *"
            )
            stmt.setObject(1, UUID.fromString(option.idSoal))
            stmt.setString(2, option.opsiText)
            stmt.setInt(3, option.opsiOrder)
            stmt.setBoolean(4, option.isCorrect)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                AnswerOptionEntity(
                    id = rs.getString("id"),
                    idSoal = rs.getString("id_soal"),
                    opsiText = rs.getString("opsi_text"),
                    opsiOrder = rs.getInt("opsi_order"),
                    isCorrect = rs.getBoolean("is_correct")
                )
            } else null
        }
    }

    fun deleteOptionsByQuestionId(idSoal: String): Boolean {
        return transaction(db) {
            val connection = TransactionManager.current().connection.connection as Connection
            val stmt = connection.prepareStatement("DELETE FROM opsi_jawaban WHERE id_soal = ?")
            stmt.setObject(1, UUID.fromString(idSoal))
            stmt.executeUpdate() > 0
        }
    }
}