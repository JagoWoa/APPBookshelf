package com.example.appbookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appbookshelf.ui.BookshelfApp
import com.example.appbookshelf.ui.theme.APPBookshelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            APPBookshelfTheme {
                BookshelfApp()
            }
        }
    }
}

