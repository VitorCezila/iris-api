package com.ghn.data.responses

data class ProfileResponse(
    val userId: String,
    val username: String,
    val bio: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val profilePictureBase64: String,
    val profileBannerBase64: String?,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
)
