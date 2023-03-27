package com.ghn.data.util

sealed class NotificationType(val type: Int) {
    object LikedPost: NotificationType(0)
    object LikedComment: NotificationType(1)
    object CommentOnPost: NotificationType(2)
    object FollowedUser: NotificationType(3)
}
