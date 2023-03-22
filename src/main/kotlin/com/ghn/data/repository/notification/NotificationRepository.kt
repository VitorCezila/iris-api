package com.ghn.data.repository.notification

import com.ghn.data.models.Notification
import com.ghn.data.responses.NotificationResponse
import com.ghn.util.Constants

interface NotificationRepository {

    suspend fun getNotificationsForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_NOTIFICATION_PAGE_SIZE
    ): List<NotificationResponse>

    suspend fun createNotification(notification: Notification)

    suspend fun deleteNotification(notificationId: String): Boolean
}