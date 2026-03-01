package com.cbt.modules.questions

import com.cbt.entities.AnswerOptionEntity
import com.cbt.entities.QuestionEntity
import com.cbt.modules.questions.dto.AnswerOptionResponse
import com.cbt.modules.questions.dto.CreateQuestionRequest
import com.cbt.modules.questions.dto.QuestionResponse

class QuestionService(private val questionRepository: QuestionRepository) {

    fun getAll(): List<QuestionResponse> {
        return questionRepository.findAll().map { question ->
            val options = questionRepository.findOptionsByQuestionId(question.id)
            question.toResponse(options)
        }
    }

    fun getById(id: String): QuestionResponse? {
        val question = questionRepository.findById(id) ?: return null
        val options = questionRepository.findOptionsByQuestionId(id)
        return question.toResponse(options)
    }

    fun getByExamId(idUjian: String): List<QuestionResponse> {
        return questionRepository.findByExamId(idUjian).map { question ->
            val options = questionRepository.findOptionsByQuestionId(question.id)
            question.toResponse(options)
        }
    }

    fun create(request: CreateQuestionRequest): QuestionResponse? {
        val question = QuestionEntity(
            id = "",
            idUjian = request.idUjian,
            tipeSoal = request.tipeSoal,
            teksSoal = request.teksSoal,
            image = request.image,
            poin = request.poin
        )
        val created = questionRepository.create(question) ?: return null

        request.opsiJawaban.forEach { opsi ->
            questionRepository.createOption(
                AnswerOptionEntity(
                    id = "",
                    idSoal = created.id,
                    opsiText = opsi.opsiText,
                    opsiOrder = opsi.opsiOrder,
                    isCorrect = opsi.isCorrect
                )
            )
        }

        // fetch ulang opsi dari DB
        val options = questionRepository.findOptionsByQuestionId(created.id)
        return created.toResponse(options)
    }

    fun update(id: String, request: CreateQuestionRequest): QuestionResponse? {
        val question = QuestionEntity(
            id = id,
            idUjian = request.idUjian,
            tipeSoal = request.tipeSoal,
            teksSoal = request.teksSoal,
            image = request.image,
            poin = request.poin
        )
        val updated = questionRepository.update(id, question) ?: return null

        // hapus opsi lama, insert yang baru
        questionRepository.deleteOptionsByQuestionId(id)
        request.opsiJawaban.forEach { opsi ->
            questionRepository.createOption(
                AnswerOptionEntity(
                    id = "",
                    idSoal = id,
                    opsiText = opsi.opsiText,
                    opsiOrder = opsi.opsiOrder,
                    isCorrect = opsi.isCorrect
                )
            )
        }

        // fetch ulang opsi dari DB
        val options = questionRepository.findOptionsByQuestionId(id)
        return updated.toResponse(options)
    }

    fun delete(id: String): Boolean {
        questionRepository.deleteOptionsByQuestionId(id)
        return questionRepository.delete(id)
    }

    private fun QuestionEntity.toResponse(options: List<AnswerOptionEntity> = emptyList()) = QuestionResponse(
        id = id,
        idUjian = idUjian,
        tipeSoal = tipeSoal,
        teksSoal = teksSoal,
        image = image,
        poin = poin,
        opsiJawaban = options.map { it.toResponse() }
    )

    private fun AnswerOptionEntity.toResponse() = AnswerOptionResponse(
        id = id,
        idSoal = idSoal,
        opsiText = opsiText,
        opsiOrder = opsiOrder,
        isCorrect = isCorrect
    )
}