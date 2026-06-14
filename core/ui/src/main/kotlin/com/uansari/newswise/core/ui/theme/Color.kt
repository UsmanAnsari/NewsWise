package com.uansari.newswise.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Blue80 = Color(0xFF81D4FA)
val BlueGrey80 = Color(0xFFB0BEC5)
val Teal80 = Color(0xFF80DEEA)

val Blue40 = Color(0xFF0288D1)
val BlueGrey40 = Color(0xFF546E7A)
val Teal40 = Color(0xFF0097A7)

internal val LightColorScheme = lightColorScheme(
    primary = Blue40, secondary = BlueGrey40, tertiary = Teal40
)

internal val DarkColorScheme = darkColorScheme(
    primary = Blue80, secondary = BlueGrey80, tertiary = Teal80
)