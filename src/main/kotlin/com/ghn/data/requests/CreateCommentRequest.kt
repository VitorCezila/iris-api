package com.ghn.data.requests

data class CreateCommentRequest(
    val content: String,
    val postId: String,
)
