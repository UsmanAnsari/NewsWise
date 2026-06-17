package com.uansari.newswise.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HeadlinesRoute

@Serializable
data object SearchRoute

@Serializable
data object BookmarksRoute

@Serializable
data class ArticleDetailRoute(val articleUrl: String) {
    companion object {
        const val ARG_URL = "articleUrl"
    }
}