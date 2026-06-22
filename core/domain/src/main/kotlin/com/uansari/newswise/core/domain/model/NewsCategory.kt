package com.uansari.newswise.core.domain.model

enum class NewsCategory(val value: String, val displayName: String) {
    GENERAL("general", "Top Stories"),
    BUSINESS("business", "Business"),
    SCIENCE("science", "Science"),
    TECHNOLOGY("technology", "Technology"),
    ENTERTAINMENT("entertainment", "Entertainment"),
    SPORTS("sports", "Sports"),
    HEALTH("health", "Health")
}