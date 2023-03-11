package com.ghn.routes

import com.ghn.data.requests.FollowUpdateRequest
import com.ghn.data.responses.BasicApiResponse
import com.ghn.service.FollowService
import com.ghn.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(followService: FollowService) {
    post("/following/follow") {
        val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = followService.followUserIfExists(request, request.followedUserId)
        if (didUserExist) {
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
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}

fun Route.unfollowUser(followService: FollowService) {

}