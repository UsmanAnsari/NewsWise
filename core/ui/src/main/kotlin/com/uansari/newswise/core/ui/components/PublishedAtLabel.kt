package com.uansari.newswise.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uansari.newswise.core.ui.utils.toExactDateTime
import com.uansari.newswise.core.ui.utils.toRelativeTime

@Composable
fun PublishedAtLabel(
    publishedAt: String, modifier: Modifier = Modifier
) {
    var showExact by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = showExact,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "published_at_toggle",
        modifier = modifier
            .clickable { showExact = !showExact }
            .padding(4.dp)) { isExact ->
        Text(
            text = if (isExact) publishedAt.toExactDateTime()
            else publishedAt.toRelativeTime(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}