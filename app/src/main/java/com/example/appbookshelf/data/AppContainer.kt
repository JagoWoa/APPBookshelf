package com.example.appbookshelf.data

import com.example.appbookshelf.BuildConfig
import com.example.appbookshelf.network.BooksApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val booksRepository: BooksRepository
}

class DefaultAppContainer : AppContainer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val authenticatedRequest = if (BuildConfig.BOOKS_API_KEY.isNotBlank()) {
                request.newBuilder().url(
                    request.url.newBuilder()
                        .addQueryParameter("key", BuildConfig.BOOKS_API_KEY)
                        .build()
                ).build()
            } else {
                request
            }
            chain.proceed(authenticatedRequest)
        }
        .build()

    private val service: BooksApiService = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BooksApiService::class.java)

    override val booksRepository: BooksRepository by lazy { NetworkBooksRepository(service) }
}
