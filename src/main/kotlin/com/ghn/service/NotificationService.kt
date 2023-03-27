package com.ghn.service

import com.ghn.data.models.Notification
import com.ghn.data.repository.comment.CommentRepository
import com.ghn.data.repository.notification.NotificationRepository
import com.ghn.data.repository.post.PostRepository
import com.ghn.data.responses.NotificationResponse
import com.ghn.data.util.NotificationType
import com.ghn.data.util.ParentType
import com.ghn.util.Constants

class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {

    suspend fun getNotificationsForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_NOTIFICATION_PAGE_SIZE
    ): List<NotificationResponse> {
        return notificationRepository.getNotificationsForUser(userId, page, pageSize)
    }

    suspend fun addCommentNotification(
        byUserId: String,
        postId: String
    ): Boolean {
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false
        if (byUserId == userIdOfPost) {
            return false
        }
        notificationRepository.createNotification(
            Notification(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = userIdOfPost,
                type = NotificationType.CommentOnPost.type,
                parentId = postId
            )
        )
        return true
    }

    suspend fun addLikeNotification(
        byUserId: String,
        parentType: ParentType,
        parentId: String
    ): Boolean {
        val toUserId = when(parentType) {
            is ParentType.Post -> {
                postRepository.getPost(parentId)?.userId
            }
            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }
            is ParentType.None -> return false
        } ?: return false
        if(byUserId == toUserId) {
            return false
        }
        notificationRepository.createNotification(
            Notification(
                parentId = parentId,
                toUserId = toUserId,
                byUserId = byUserId,
                type = when(parentType) {
                    is ParentType.Post -> NotificationType.LikedPost.type
                    is ParentType.Comment -> NotificationType.LikedComment.type
                    else -> NotificationType.LikedPost.type
                },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun createNotification(notification: Notification) {
        notificationRepository.createNotification(notification)
    }
}