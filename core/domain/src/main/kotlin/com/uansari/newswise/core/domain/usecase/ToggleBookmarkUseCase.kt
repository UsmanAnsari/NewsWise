package com.uansari.newswise.core.domain.usecase

import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(url: String) = repository.toggleBookmark(url)
}