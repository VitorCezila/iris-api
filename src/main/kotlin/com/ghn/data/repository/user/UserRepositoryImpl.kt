package com.ghn.data.repository.user

import com.ghn.data.models.User
import com.ghn.data.requests.UpdateProfileRequest
import org.litote.kmongo.`in`
import org.litote.kmongo.or
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.regex


class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {

    private val users: CoroutineCollection<User> = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        val user = getUserById(userId) ?: return false
        return users.updateOneById(
            id = userId,
            update = User(
                id = user.id,
                email = user.email,
                username = updateProfileRequest.username,
                password = user.password,
                profileImageUrl = profileImageUrl ?: user.profileImageUrl,
                bannerUrl = bannerUrl ?: user.bannerUrl,
                bio = updateProfileRequest.bio,
                followerCount = user.followerCount,
                followingCount = user.followingCount,
                postCount = user.postCount
            )
        ).wasAcknowledged()
    }

    override suspend fun doesPasswordForUserMatch(
        email: String,
        enteredPassword: String
    ): Boolean {
        val user = getUserByEmail(email)
        return user?.password == enteredPassword
    }

    override suspend fun doesEmailBelongToUserId(
        email: String,
        userId: String
    ): Boolean {
        return users.findOneById(userId)?.email == email
    }

    override suspend fun searchForUsers(query: String): List<User> {
        return users.find(
            or(
                User::username regex Regex("(?i).*$query.*"),
                User::email eq query
            )
        ).descendingSort(User::followerCount).toList()
    }

    override suspend fun getUsers(userIds: List<String>): List<User> {
        return users.find(User::id `in` userIds).toList()
    }
}