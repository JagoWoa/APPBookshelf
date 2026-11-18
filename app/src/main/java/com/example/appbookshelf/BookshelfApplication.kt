package com.example.appbookshelf

import android.app.Application
import com.example.appbookshelf.data.AppContainer
import com.example.appbookshelf.data.DefaultAppContainer

class BookshelfApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer() }
}
