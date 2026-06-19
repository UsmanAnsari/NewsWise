package com.uansari.newswise.core.ui.utils

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun String.toRelativeTime(): String {
    return try {
        val published = Instant.parse(this)
        val now = Instant.now()
        val duration = Duration.between(published, now)
        val seconds = duration.seconds

        when {
            seconds < 0 -> "Just now"
            seconds < 60 -> "Just now"
            seconds < 3_600 -> "${seconds / 60}m ago"
            seconds < 86_400 -> "${seconds / 3_600}h ago"
            seconds < 604_800 -> "${seconds / 86_400}d ago"
            seconds < 2_592_000 -> "${seconds / 604_800}w ago"
            seconds < 31_536_000 -> "${seconds / 2_592_000}mo ago"
            else -> "${seconds / 31_536_000}y ago"
        }
    } catch (e: Exception) {
        this.take(10)
    }
}

internal fun String.toExactDateTime(): String {
    return try {
        val instant = Instant.parse(this)
        val formatter =
            DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a").withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        this.take(10)
    }
}