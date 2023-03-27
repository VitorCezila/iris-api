package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Notification(
    @BsonId
    val id: String = ObjectId().toString(),
    val parentId: String,
    val type: Int,
    val toUserId: String,
    val byUserId: String,
    val timestamp: Long
)
