package com.ghn.data.util

sealed class ActivityType(val type: Int) {
    object LikedPost: ActivityType(0)
    object LikedComment: ActivityType(1)
    object CommentOnPost: ActivityType(2)
    object FollowedUser: ActivityType(3)
}
