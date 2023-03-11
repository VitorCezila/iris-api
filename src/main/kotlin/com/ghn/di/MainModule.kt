package com.ghn.di

import com.ghn.data.repository.user.UserRepository
import com.ghn.data.repository.user.UserRepositoryImpl
import com.ghn.service.UserService
import com.ghn.util.Constants
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {

    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<UserService> {
        UserService(get())
    }

    single { Gson() }

}