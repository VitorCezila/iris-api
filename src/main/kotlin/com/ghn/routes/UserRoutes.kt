package com.ghn.routes

import com.ghn.data.requests.UpdateProfileRequest
import com.ghn.data.responses.BasicApiResponse
import com.ghn.data.responses.UserResponseItem
import com.ghn.service.UserService
import com.ghn.util.ApiResponseMessages
import com.ghn.util.Constants.BANNER_IMAGE_PATH
import com.ghn.util.Constants.PROFILE_PICTURE_PATH
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
            if (userId == null || userId.isBlank()) {
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
            var profilePictureFileName: String? = null
            var bannerImageFileName: String? = null
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
                            profilePictureFileName = partData.save(PROFILE_PICTURE_PATH)
                        } else if (partData.name == "banner_image") {
                            bannerImageFileName = partData.save(BANNER_IMAGE_PATH)
                        }
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }

            val profilePictureUrl = "${System.getenv("BASE_URL")}/profile_pictures/$profilePictureFileName"
            val bannerImageUrl = "${System.getenv("BASE_URL")}/banner_images/$bannerImageFileName"

            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = if (profilePictureFileName == null) {
                        null
                    } else {
                        profilePictureUrl
                    },
                    bannerUrl = if (bannerImageFileName == null) {
                        null
                    } else {
                        bannerImageUrl
                    },
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
                    File("${PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}