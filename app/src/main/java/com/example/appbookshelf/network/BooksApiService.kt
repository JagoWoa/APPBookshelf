package com.example.appbookshelf.network

import com.example.appbookshelf.model.BookVolume
import com.example.appbookshelf.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("printType") printType: String = "books",
    ): BooksResponse

    @GET("volumes/{volumeId}")
    suspend fun getBook(@Path("volumeId") volumeId: String): BookVolume
}
