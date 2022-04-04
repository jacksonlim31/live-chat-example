package com.carousell.sample.di

import com.carousell.sample.remotes.ChatDemoClient
import com.carousell.sample.remotes.services.ChatDemoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideChatDemoApi(): ChatDemoApiService {
        return ChatDemoClient.getInstances("https://gateway.chotot.org/")
    }
}