package com.ghn.data.repository.post

import com.ghn.data.models.Following
import com.ghn.data.models.Post
import com.ghn.data.models.User
import com.ghn.data.responses.PostResponse
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.inc

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged().also { wasAcknowledged ->
            if (wasAcknowledged) {
                users.updateOneById(
                    post.userId,
                    inc(User::postCount, 1)
                )
            }
        }
    }

    override suspend fun deletePost(postId: String) {
        posts.findOneById(postId)?.also {
            users.updateOneById(
                it.userId,
                inc(User::postCount, -1)
            )
        }
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int,
        pageSize: Int
    ): List<PostResponse> {
        val userIdsFromFollows = following.find(Following::followingUserId eq ownUserId)
            .toList()
            .map {
                it.followedUserId
            }
        return posts.find(Post::userId `in` userIdsFromFollows)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { post ->
                val user = users.findOneById(post.userId)
                PostResponse(
                    id = post.id,
                    userId = ownUserId,
                    username = user?.username ?: "",
                    imageUrl = post.imageUrl,
                    profilePictureUrl = user?.profileImageUrl ?: "",
                    description = post.content,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = true,
                    isOwnPost = ownUserId == post.userId
                )
            }
    }

    override suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int,
        pageSize: Int
    ): List<PostResponse> {
        val user = users.findOneById(userId) ?: return emptyList()
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { post ->
                PostResponse(
                    id = post.id,
                    userId = userId,
                    username = user.username,
                    imageUrl = post.imageUrl,
                    profilePictureUrl = user.profileImageUrl,
                    description = post.content,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = true,
                    isOwnPost = ownUserId == post.userId
                )
            }
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }

    override suspend fun getPostDetails(userId: String, postId: String): PostResponse? {
        val post = posts.findOneById(postId) ?: return null
        val user = users.findOneById(post.userId) ?: return null
        return PostResponse(
            id = post.id,
            userId = user.id,
            username = user.username,
            imageUrl = post.imageUrl,
            profilePictureUrl = user.profileImageUrl,
            description = post.content,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            isLiked = true,
            isOwnPost = userId == post.userId
        )
    }
}