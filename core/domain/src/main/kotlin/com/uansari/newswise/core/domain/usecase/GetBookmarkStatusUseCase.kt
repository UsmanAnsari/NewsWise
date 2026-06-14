package com.uansari.newswise.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

class GetBookmarkStatusUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    operator fun invoke(url: String): Flow<Boolean> = repository.observeBookmarkStatus(url)
}