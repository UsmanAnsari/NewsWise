package com.uansari.newswise.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HeadlinesRoute

@Serializable
data object SearchRoute

@Serializable
data object BookmarksRoute

// articleUrl: the URL passed to the detail screen.
// Why URL not ID? Articles have no integer ID — URL is the natural key.
// Why not pass the full Article object? Objects in navigation arguments must be
// Parcelable or Serializable. Passing IDs (primitives) is safer and
// keeps navigation contracts thin.
@Serializable
data class ArticleDetailRoute(val articleUrl: String)