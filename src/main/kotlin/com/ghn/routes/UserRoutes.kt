package com.ghn.routes

import com.ghn.data.requests.UpdateProfileRequest
import com.ghn.data.responses.BasicApiResponse
import com.ghn.data.responses.UserResponseItem
import com.ghn.service.UserService
import com.ghn.util.ApiResponseMessages
import com.ghn.util.QueryParams
import com.ghn.util.convertToBase64
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query == null || query.isBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<UserResponseItem>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResults
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if (profileResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = profileResponse
                )
            )
        }
    }
}

fun Route.updateUserProfile(userService: UserService) {
    val gson: Gson by inject()
    authenticate {
        put("/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null

            var profilePictureBase64: String? = null
            var profileBannerBase64: String? = null

            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        if (partData.name == "profile_picture") {
                            profilePictureBase64 = partData.convertToBase64()
                        } else if (partData.name == "banner_image") {
                            profileBannerBase64 = partData.convertToBase64()
                        }
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }

            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profilePictureBase64 = profilePictureBase64,
                    profileBannerBase64 = profileBannerBase64,
                    updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}