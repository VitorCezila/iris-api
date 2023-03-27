package com.ghn.data.repository.comment

import com.ghn.data.models.Comment
import com.ghn.data.responses.CommentResponse

interface CommentRepository {

    suspend fun createComment(comment: Comment): String

    suspend fun deleteComment(commentId: String): Boolean

    suspend fun deleteCommentsFromPost(postId: String): Boolean

    suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse>

    suspend fun getComment(commentId: String): Comment?

}
