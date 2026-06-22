package com.uansari.newswise.display.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uansari.newswise.core.ui.theme.NewsWiseTheme
import com.uansari.newswise.display.NewsWiseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsWiseTheme {
                NewsWiseApp()
            }
        }
    }
}