package com.uansari.newswise.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.repository.InlineFakeRepository
import com.uansari.newswise.core.domain.repository.makeTestArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpsertArticleUseCaseTest {

    private val repository = InlineFakeRepository()
    private val useCase = UpsertArticleUseCase(repository)

    @Test
    fun `inserts article when not previously stored`() = runTest {
        val article = makeTestArticle()
        useCase(article)

        val result = repository.observeArticle(article.url).first()
        assertThat(result).isNotNull()
        assertThat(result?.url).isEqualTo(article.url)
    }

    @Test
    fun `updates article content when already present`() = runTest {
        val original = makeTestArticle()
        useCase(original)

        val updated = original.copy(title = "Updated Title")
        useCase(updated)

        val result = repository.observeArticle(original.url).first()
        assertThat(result?.title).isEqualTo("Updated Title")
    }

    @Test
    fun `preserves isBookmarked when upserting over existing bookmarked article`() = runTest {
        val bookmarked = makeTestArticle().copy(isBookmarked = true)
        useCase(bookmarked)

        useCase(bookmarked.copy(isBookmarked = false))

        val result = repository.observeArticle(bookmarked.url).first()
        assertThat(result?.isBookmarked).isTrue()
    }

    @Test
    fun `inserting same article twice does not duplicate it`() = runTest {
        val article = makeTestArticle()
        useCase(article)
        useCase(article)

        val result = repository.observeArticle(article.url).first()
        assertThat(result?.url).isEqualTo(article.url)
    }
}
