package com.uansari.newswise.di

import com.uansari.newswise.BuildConfig
import com.uansari.newswise.core.network.config.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = NetworkConfig(
        baseUrl = "https://newsapi.org/v2/",
        // BuildConfig.NEWS_API_KEY is injected from local.properties via
        // the buildConfigField() in app/build.gradle.kts.
        apiKey = BuildConfig.NEWS_API_KEY
    )
}