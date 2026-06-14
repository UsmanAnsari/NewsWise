package com.uansari.newswise.core.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

class SearchArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> = repository.searchArticles(query)
}