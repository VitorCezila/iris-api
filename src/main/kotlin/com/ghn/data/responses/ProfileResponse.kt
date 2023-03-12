package com.ghn.data.responses

data class ProfileResponse(
    val userId: String,
    val username: String,
    val bio: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val profilePictureUrl: String,
    val bannerUrl: String?,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
)
