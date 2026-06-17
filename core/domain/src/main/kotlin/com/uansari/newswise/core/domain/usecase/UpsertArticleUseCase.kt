package com.uansari.newswise.core.domain.usecase

import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

class UpsertArticleUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(article: Article) = repository.upsertArticle(article)
}
