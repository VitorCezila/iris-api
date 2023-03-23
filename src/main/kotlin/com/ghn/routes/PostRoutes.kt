package com.ghn.routes

import com.ghn.data.requests.CreatePostRequest
import com.ghn.data.responses.BasicApiResponse
import com.ghn.service.CommentService
import com.ghn.service.LikeService
import com.ghn.service.PostService
import com.ghn.util.Constants
import com.ghn.util.QueryParams
import com.ghn.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import javax.management.Query

fun Route.createPost(postService: PostService) {
    val gson by inject<Gson>()
    authenticate {
        post("/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                partData.value,
                                CreatePostRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(Constants.POST_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }

            val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
                val createPostAcknowledged = postService.createPost(
                    request = request,
                    userId = call.userId,
                    imageUrl = postPictureUrl
                )
                if (createPostAcknowledged) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

        }
    }
}

fun Route.getPostForProfile(
    postService: PostService
) {
    authenticate {
        get("/user/post") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

            val posts = postService.getPostsForProfile(
                ownUserId = call.userId,
                userId = userId ?: call.userId,
                page = page,
                pageSize = pageSize
            )
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.getPostForFollows(
    postService: PostService
) {
    authenticate {
        get("/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE
            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
) {
    authenticate {
        delete("/post/delete") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.OK)
                return@delete
            }
            val post = postService.getPost(postId)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            if(post.userId == call.userId) {
                postService.deletePost(postId)
                likeService.deleteLikesForParent(postId)
                commentService.deleteComment(postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

fun Route.getPostDetails(postService: PostService) {
    get("/post/details") {
        val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val post = postService.getPostDetails(call.userId, postId) ?: kotlin.run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(
            HttpStatusCode.OK,
            BasicApiResponse(
                successful = true,
                data = post
            )
        )
    }
}