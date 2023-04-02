package com.ghn.di

import com.ghn.data.repository.comment.CommentRepository
import com.ghn.data.repository.comment.CommentRepositoryImpl
import com.ghn.data.repository.follow.FollowRepository
import com.ghn.data.repository.follow.FollowRepositoryImpl
import com.ghn.data.repository.likes.LikeRepository
import com.ghn.data.repository.likes.LikeRepositoryImpl
import com.ghn.data.repository.notification.NotificationRepository
import com.ghn.data.repository.notification.NotificationRepositoryImpl
import com.ghn.data.repository.post.PostRepository
import com.ghn.data.repository.post.PostRepositoryImpl
import com.ghn.data.repository.user.UserRepository
import com.ghn.data.repository.user.UserRepositoryImpl
import com.ghn.service.*
import com.ghn.util.Constants
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {

    single {
        val client = KMongo.createClient(System.getenv("MONGO_URI")).coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single<NotificationRepository> {
        NotificationRepositoryImpl(get())
    }

    single<UserService> {
        UserService(get(), get())
    }
    single<FollowService> {
        FollowService(get())
    }
    single<PostService> {
        PostService(get())
    }
    single<CommentService> {
        CommentService(get(), get())
    }
    single<LikeService> {
        LikeService(get(), get(), get())
    }
    single<NotificationService> {
        NotificationService(get(), get(), get())
    }

    single { Gson() }

}