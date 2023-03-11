package com.ghn.service

import com.ghn.data.models.User
import com.ghn.data.repository.user.UserRepository
import com.ghn.data.requests.CreateAccountRequest

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun doesUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    suspend fun createUser(request: CreateAccountRequest) {
        userRepository.createUser(User(
            email = request.email,
            username = request.username,
            password = request.password,
            profileImageUrl = "",
            bannerUrl = "",
            bio = ""
        ))
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent {
        if(request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean {
        return enteredPassword == actualPassword
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}