package com.ghn.data.responses

data class PostResponse(
    val id: String,
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val content: String,
    val imageUrl: String,
    val timestamp: Long,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean,
    val isOwnPost: Boolean
)
