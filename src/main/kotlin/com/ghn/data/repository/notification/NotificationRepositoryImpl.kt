package com.ghn.data.repository.notification

import com.ghn.data.models.Notification
import com.ghn.data.models.User
import com.ghn.data.responses.NotificationResponse
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class NotificationRepositoryImpl(
    private val db: CoroutineDatabase
) : NotificationRepository {

    private val users = db.getCollection<User>()
    private val notifications = db.getCollection<Notification>()

    override suspend fun getNotificationsForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<NotificationResponse> {
        val notifications = notifications.find(Notification::toUserId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Notification::timestamp)
            .toList()
        val userIds = notifications.map { it.byUserId }
        val users = users.find(User::id `in` userIds).toList()
        return notifications.mapIndexed { i, notification ->
            NotificationResponse(
                timestamp = notification.timestamp,
                userId = notification.byUserId,
                parentId = notification.parentId,
                type = notification.type,
                username = users[i].username,
                id = notification.id
            )
        }
    }

    override suspend fun createNotification(notification: Notification) {
        notifications.insertOne(notification)
    }

    override suspend fun deleteNotification(notificationId: String): Boolean {
        return notifications.deleteOneById(notificationId).wasAcknowledged()
    }
}