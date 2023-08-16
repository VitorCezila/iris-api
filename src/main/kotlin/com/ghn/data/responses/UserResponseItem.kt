package com.ghn.data.responses

data class UserResponseItem(
    val userId: String,
    val username: String,
    val profilePictureBase64: String,
    val bio: String,
    val isFollowing: Boolean
)
