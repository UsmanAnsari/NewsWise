package com.uansari.newswise.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.repository.InlineFakeRepository
import com.uansari.newswise.core.domain.repository.makeTestArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetArticleByUrlUseCaseTest {

    private val repository = InlineFakeRepository()
    private val useCase = GetArticleByUrlUseCase(repository)

    @Test
    fun `returns null when article not in repository`() = runTest {
        val result = useCase("https://example.com/article").first()
        assertThat(result).isNull()
    }

    @Test
    fun `returns article when present in repository`() = runTest {
        val article = makeTestArticle("https://example.com/article")
        repository.upsertArticle(article)

        val result = useCase("https://example.com/article").first()
        assertThat(result).isNotNull()
        assertThat(result?.url).isEqualTo(article.url)
    }

    @Test
    fun `returns correct article when multiple articles exist`() = runTest {
        val article1 = makeTestArticle("https://example.com/article-1")
        val article2 = makeTestArticle("https://example.com/article-2")
        repository.upsertArticle(article1)
        repository.upsertArticle(article2)

        val result = useCase("https://example.com/article-2").first()
        assertThat(result?.url).isEqualTo("https://example.com/article-2")
    }

    @Test
    fun `returns null for url that does not match any article`() = runTest {
        val article = makeTestArticle("https://example.com/article")
        repository.upsertArticle(article)

        val result = useCase("https://example.com/different").first()
        assertThat(result).isNull()
    }
}
