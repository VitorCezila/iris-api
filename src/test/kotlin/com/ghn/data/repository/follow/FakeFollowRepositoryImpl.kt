package com.ghn.data.repository.follow

import com.ghn.data.models.Following

class FakeFollowRepositoryImpl: FollowRepository {
    override suspend fun followUserIfExists(followingUserId: String, followedUserId: String): Boolean {
        return false
    }

    override suspend fun unfollowUserIfExists(followingUserId: String, followedUserId: String): Boolean {
        return false
    }

    override suspend fun getFollowsByUser(userId: String): List<Following> {
        return listOf()
    }

    override suspend fun doesUserFollow(followingUserId: String, followedUserId: String): Boolean {
        return false
    }
}