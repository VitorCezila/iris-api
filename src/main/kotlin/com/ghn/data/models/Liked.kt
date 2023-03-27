package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Liked(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String,
    val parentId: String
)
