package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    @BsonId
    val id: String = ObjectId().toString(),
    val content: String,
    val username: String,
    val profilePictureBase64: String,
    val userId: String,
    val postId: String,
    val timestamp: Long,
    val likeCount: Int,
)
