package com.uansari.newswise.core.domain.model

enum class NewsCategory(val value: String, val displayName: String) {
    GENERAL("general", "Top Stories"),
    BUSINESS("business", "Business"),
    TECHNOLOGY("technology", "Technology"),
    SCIENCE("science", "Science"),
    HEALTH("health", "Health")
}