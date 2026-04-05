package com.cbt.modules.exams

import com.cbt.entities.ExamEntity
import com.cbt.modules.exams.dto.CreateExamRequest
import com.cbt.modules.exams.dto.ExamResponse

class ExamService(private val examRepository: ExamRepository) {

    fun getAll(): List<ExamResponse> {
        return examRepository.findAll().map { it.toResponse() }
    }

    fun getById(id: String): ExamResponse? {
        return examRepository.findById(id)?.toResponse()
    }

    // --- TAMBAHKAN FUNGSI INI ---
    fun getByToken(token: String): ExamResponse? {
        // Service manggil Repository buat nyari data mentah (Entity)
        // Terus diubah jadi format JSON (Response) pake .toResponse()
        return examRepository.findByToken(token)?.toResponse()
    }

    fun create(request: CreateExamRequest, createdBy: String): ExamResponse? {
        val exam = ExamEntity(
            id = "",
            judul = request.judul,
            idMapel = request.idMapel,
            durasi = request.durasi,
            totalSoal = request.totalSoal,
            startTime = request.startTime,
            endTime = request.endTime,
            status = request.status,
            soalRandom = request.soalRandom,
            jawabanRandom = request.jawabanRandom,
            createdBy = createdBy
        )
        return examRepository.create(exam, createdBy)?.toResponse()
    }

    fun update(id: String, request: CreateExamRequest): ExamResponse? {
        val exam = ExamEntity(
            id = id,
            judul = request.judul,
            idMapel = request.idMapel,
            durasi = request.durasi,
            totalSoal = request.totalSoal,
            startTime = request.startTime,
            endTime = request.endTime,
            status = request.status,
            soalRandom = request.soalRandom,
            jawabanRandom = request.jawabanRandom,
            createdBy = null
        )
        return examRepository.update(id, exam)?.toResponse()
    }

    fun delete(id: String): Boolean {
        return examRepository.delete(id)
    }

    private fun ExamEntity.toResponse() = ExamResponse(
        id = id,
        judul = judul,
        idMapel = idMapel,
        durasi = durasi,
        totalSoal = totalSoal,
        startTime = startTime,
        endTime = endTime,
        status = status,
        soalRandom = soalRandom,
        jawabanRandom = jawabanRandom,
        createdBy = createdBy
    )
}