package com.ghn.routes

import com.ghn.data.requests.LikeUpdateRequest
import com.ghn.data.responses.BasicApiResponse
import com.ghn.service.LikeService
import com.ghn.util.ApiResponseMessages
import com.ghn.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
    likeService: LikeService
) {
    authenticate {
        post("/like") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            val likeSuccessful = likeService.likeParent(userId, request.parentId, request.parentType)
            if (likeSuccessful) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(successful = true)
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(successful = false, message = ApiResponseMessages.USER_NOT_FOUND)
                )
            }
        }
    }
}

fun Route.unlikeParent(
    likeService: LikeService
) {
    authenticate {
        delete("/unlike") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val parentType = call.parameters[QueryParams.PARAM_PARENT_TYPE]?.toIntOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val unlikeSuccessful = likeService.unlikeParent(call.userId, parentId, parentType)
            if (unlikeSuccessful) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.getLikesForParent(likeService: LikeService) {
    authenticate {
        get("/like/parent") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val usersWhoLikedParent = likeService.getUsersWhoLikedParent(parentId, call.userId)
            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )
        }
    }
}