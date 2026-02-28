package com.cbt.modules.users

import com.cbt.modules.users.dto.CreateUserRequest
import com.cbt.modules.users.dto.UserResponse
import com.cbt.utils.PasswordUtil

class UserService(private val userRepository: UserRepository) {

    fun getAll(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    fun getById(id: String): UserResponse? {
        return userRepository.findById(id)?.toResponse()
    }

    fun create(request: CreateUserRequest): UserResponse? {
        val passwordHash = PasswordUtil.hash(request.password)
        return userRepository.create(
            nama = request.nama,
            nisnNip = request.nisnNip,
            passwordHash = passwordHash,
            role = request.role,
            idKelas = request.idKelas
        )?.toResponse()
    }

    fun update(id: String, request: CreateUserRequest): UserResponse? {
        return userRepository.update(
            id = id,
            nama = request.nama,
            nisnNip = request.nisnNip,
            role = request.role,
            idKelas = request.idKelas
        )?.toResponse()
    }

    fun delete(id: String): Boolean {
        return userRepository.delete(id)
    }

    private fun com.cbt.entities.UserEntity.toResponse() = UserResponse(
        id = id,
        nama = nama,
        nisnNip = nisnNip,
        role = role,
        idKelas = idKelas,
        tanggalDibuat = tanggalDibuat
    )
}