package com.ghn.data.repository.user

import com.ghn.data.models.User
import com.ghn.data.requests.UpdateProfileRequest
import com.ghn.util.Constants
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.or
import org.litote.kmongo.regex
import java.util.*


class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {

    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        val profileDefaultPicture = javaClass.classLoader.getResource(Constants.DEFAULT_PROFILE_PICTURE_PATH).readBytes()
        val profileDefaultPictureBase64 = Base64.getEncoder().encodeToString(profileDefaultPicture)

        val profileDefaultBanner = javaClass.classLoader.getResource(Constants.DEFAULT_BANNER_IMAGE_PATH).readBytes()
        val profileDefaultBannerBase64 = Base64.getEncoder().encodeToString(profileDefaultBanner)

        val newUser = user.copy(
            profilePictureBase64 = profileDefaultPictureBase64,
            profileBannerBase64 = profileDefaultBannerBase64,
        )

        users.insertOne(newUser)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun updateUser(
        userId: String,
        profilePictureBase64: String?,
        profileBannerBase64: String?,
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
                profilePictureBase64 = profilePictureBase64 ?: user.profilePictureBase64,
                profileBannerBase64 = profileBannerBase64 ?: user.profileBannerBase64,
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