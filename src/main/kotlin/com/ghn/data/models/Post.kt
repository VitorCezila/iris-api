package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    @BsonId
    val id: String = ObjectId().toString(),
    val content: String,
    val imageBase64: String,
    val userId: String,
    val timestamp: Long,
    val likeCount: Int = 0,
    val commentCount: Int = 0
)