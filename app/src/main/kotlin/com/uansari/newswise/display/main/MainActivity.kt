package com.uansari.newswise.display.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
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
                Scaffold { innerPadding ->
                    NewsWiseApp(paddingValues = innerPadding)
                }
            }
        }
    }
}