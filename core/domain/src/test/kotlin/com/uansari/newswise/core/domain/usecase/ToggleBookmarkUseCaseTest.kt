package com.uansari.newswise.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.repository.InlineFakeRepository
import com.uansari.newswise.core.domain.repository.makeTestArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleBookmarkUseCaseTest {

    private val repository = InlineFakeRepository()
    private val useCase = ToggleBookmarkUseCase(repository)

    @Test
    fun `toggling unbookmarked article adds it to bookmarks`() = runTest {
        val article = makeTestArticle()
        repository.upsertArticle(article)

        useCase(article.url)

        assertThat(repository.getBookmarks().first()).hasSize(1)
    }

    @Test
    fun `toggling bookmarked article removes it from bookmarks`() = runTest {
        val article = makeTestArticle()
        repository.upsertArticle(article)
        repository.addBookmark(article)

        useCase(article.url)

        assertThat(repository.getBookmarks().first()).isEmpty()
    }

    @Test
    fun `toggling article not in repository does nothing`() = runTest {
        useCase("https://example.com/nonexistent")

        assertThat(repository.getBookmarks().first()).isEmpty()
    }
}
