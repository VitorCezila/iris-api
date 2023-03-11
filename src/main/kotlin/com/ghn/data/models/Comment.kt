package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    @BsonId
    val id: String = ObjectId().toString(),
    val content: String,
    val postId: String,
    val userId: String,
    val timestamp: Long
)