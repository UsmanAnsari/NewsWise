package com.uansari.newswise.core.network

import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.network.api.NewsApiService
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class NewsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: NewsApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        apiService = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build()).addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            ).build().create(NewsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun `successful response parses all non-null fields correctly`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(readJsonResource("news_response_success.json"))
                .setResponseCode(200)
        )

        val response = apiService.getTopHeadlines(
            category = "general", page = 1, pageSize = 20
        )

        assertThat(response.status).isEqualTo("ok")
        assertThat(response.totalResults).isEqualTo(2)
        assertThat(response.articles).hasSize(2)

        val first = response.articles.first()
        assertThat(first.title).isEqualTo("Test Article Title")
        assertThat(first.source.name).isEqualTo("BBC News")
        assertThat(first.source.id).isEqualTo("bbc-news")
        assertThat(first.author).isEqualTo("Jane Smith")
        assertThat(first.url).isEqualTo("https://bbc.com/article-1")
        assertThat(first.urlToImage).isEqualTo("https://bbc.com/image-1.jpg")
        assertThat(first.publishedAt).isEqualTo("2026-06-22T10:00:00Z")
        assertThat(first.content).contains("[+1234 chars]")
    }

    @Test
    fun `nullable fields are null when absent in response`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(readJsonResource("news_response_success.json"))
                .setResponseCode(200)
        )

        val response = apiService.getTopHeadlines(
            category = "general", page = 1, pageSize = 20
        )

        val second = response.articles[1]
        assertThat(second.source.id).isNull()
        assertThat(second.author).isNull()
        assertThat(second.description).isNull()
        assertThat(second.urlToImage).isNull()
        assertThat(second.content).isNull()
    }

    @Test
    fun `empty articles list is handled without exception`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(readJsonResource("news_response_empty.json"))
                .setResponseCode(200)
        )

        val response = apiService.getTopHeadlines(
            category = "general", page = 1, pageSize = 20
        )

        assertThat(response.articles).isEmpty()
        assertThat(response.totalResults).isEqualTo(0)
    }


    @Test
    fun `401 response throws HttpException`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )
        try {
            apiService.getTopHeadlines(category = "general", page = 1, pageSize = 20)
            assertThat(false).isTrue()
        } catch (e: HttpException) {
            assertThat(e.code()).isEqualTo(401)
        }
    }


    @Test
    fun `getTopHeadlines sends request to correct endpoint`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(readJsonResource("news_response_success.json"))
                .setResponseCode(200)
        )

        apiService.getTopHeadlines(category = "technology", page = 1, pageSize = 20)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("top-headlines")
        assertThat(request.path).contains("category=technology")
    }
}

private fun readJsonResource(fileName: String): String =
    object {}.javaClass.classLoader?.getResourceAsStream(fileName)?.bufferedReader()?.readText()
        ?: error("Test resource not found: $fileName — check src/test/resources/")
