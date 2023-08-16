package com.ghn.data.repository.user

import com.ghn.data.models.User
import com.ghn.data.requests.UpdateProfileRequest

class FakeUserRepositoryImpl : UserRepository {

    private val users = mutableListOf<User>()

    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.find { it.id == id }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun updateUser(
        userId: String,
        profilePictureBase64: String?,
        profileBannerBase64: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        return false
    }

    override suspend fun doesPasswordForUserMatch(email: String, enteredPassword: String): Boolean {
        val user = getUserByEmail(email)
        return user?.password == enteredPassword
    }

    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return false
    }

    override suspend fun searchForUsers(query: String): List<User> {
        return listOf()
    }

    override suspend fun getUsers(userIds: List<String>): List<User> {
        return listOf()
    }
}