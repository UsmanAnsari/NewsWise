package com.uansari.newswise.core.network.api

import com.uansari.newswise.core.network.model.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    // GET https://newsapi.org/v2/top-headlines?category=technology&page=1&pageSize=20&country=gb
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("country") country: String = "us"
    ): NewsResponseDto

    // GET https://newsapi.org/v2/everything?q=kotlin&page=1&pageSize=20&sortBy=publishedAt
    @GET("everything")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sortBy") sortBy: String = "publishedAt"
    ): NewsResponseDto
}