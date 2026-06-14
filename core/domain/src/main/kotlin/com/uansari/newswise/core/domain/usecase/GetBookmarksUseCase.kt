package com.uansari.newswise.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.getBookmarks()
}