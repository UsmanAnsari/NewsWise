package com.uansari.newswise.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetBookmarksUseCaseTest {

    private val repository = InlineFakeRepository()
    private val useCase = GetBookmarksUseCase(repository)

    @Test
    fun `returns empty list when no bookmarks exist`() = runTest {
        val result = useCase().first()
        assertThat(result).isEmpty()
    }

    @Test
    fun `returns list when bookmarks are present`() = runTest {
        val article = makeTestArticle()
        repository.addBookmark(article)

        val result = useCase().first()
        assertThat(result).hasSize(1)
        assertThat(result.first().url).isEqualTo(article.url)
    }
}