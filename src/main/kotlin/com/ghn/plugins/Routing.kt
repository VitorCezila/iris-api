package com.ghn.plugins

import com.ghn.routes.*
import com.ghn.service.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        // User routes
        authenticate()
        createUser(userService = userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService = userService)
        getUserProfile(userService = userService)
        updateUserProfile(userService = userService)

        // Following routes
        followUser(followService = followService)
        unfollowUser(followService = followService)

        // Post routes
        createPost(postService = postService)
        getPostForProfile(postService = postService)
        getPostForFollows(postService = postService)
        deletePost(
            postService = postService,
            likeService = likeService,
            commentService = commentService
        )
        getPostDetails(postService = postService)

        // Like routes
        likeParent(likeService = likeService)
        unlikeParent(likeService = likeService)
        getLikesForParent(likeService = likeService)

        // Comment routes
        createComment(commentService = commentService)
        deleteComment(
            commentService = commentService,
            likeService = likeService
        )
    }
}
