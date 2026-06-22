package com.uansari.newswise.core.domain.usecase

import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleByUrlUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    operator fun invoke(url: String): Flow<Article?> = repository.observeArticle(url)
}