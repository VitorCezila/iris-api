package com.ghn.di

import com.ghn.data.repository.follow.FakeFollowRepositoryImpl
import com.ghn.data.repository.follow.FollowRepository
import com.ghn.data.repository.user.FakeUserRepositoryImpl
import com.ghn.data.repository.user.UserRepository
import com.ghn.service.UserService
import org.koin.dsl.module

internal val testModule = module {

    single<UserRepository> {
        FakeUserRepositoryImpl()
    }
    single<FollowRepository> {
        FakeFollowRepositoryImpl()
    }
    single {
        UserService(get(), get())
    }
}