package com.uansari.newswise.core.network.di

import com.uansari.newswise.core.network.api.NewsApiService
import com.uansari.newswise.core.network.config.NetworkConfig
import com.uansari.newswise.core.network.interceptor.ApiKeyInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(networkConfig: NetworkConfig): ApiKeyInterceptor =
        ApiKeyInterceptor(networkConfig.apiKey)

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInterceptor: ApiKeyInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, json: Json, networkConfig: NetworkConfig
    ): Retrofit = Retrofit.Builder().baseUrl(networkConfig.baseUrl).client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService =
        retrofit.create(NewsApiService::class.java)
}