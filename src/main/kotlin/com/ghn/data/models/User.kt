package com.ghn.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val username: String,
    val password: String,
    val profilePictureBase64: String,
    val profileBannerBase64: String?,
    val bio: String,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0
)
