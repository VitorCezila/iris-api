package com.ghn.data.responses

data class CommentResponse(
    val id: String,
    val username: String,
    val profilePictureUrl: String,
    val timestamp: Long,
    val content: String,
    val isLiked: Boolean,
    val likeCount: Int
)
