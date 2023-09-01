package com.ghn.data.responses

data class PostResponse(
    val id: String,
    val userId: String,
    val username: String,
    val profilePictureBase64: String,
    val content: String,
    val imageBase64: String,
    val timestamp: Long,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean,
    val isOwnPost: Boolean
)
