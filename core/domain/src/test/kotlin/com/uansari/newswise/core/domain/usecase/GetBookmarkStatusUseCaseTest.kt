package com.uansari.newswise.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetBookmarkStatusUseCaseTest {

    private val repository = InlineFakeRepository()
    private val useCase = GetBookmarkStatusUseCase(repository)

    @Test
    fun `returns false when article is not bookmarked`() = runTest {
        val result = useCase("https://example.com/test").first()
        assertThat(result).isFalse()
    }

    @Test
    fun `returns true when article is bookmarked`() = runTest {
        val article = makeTestArticle("https://example.com/test")
        repository.addBookmark(article)

        val result = useCase("https://example.com/test").first()
        assertThat(result).isTrue()
    }
}