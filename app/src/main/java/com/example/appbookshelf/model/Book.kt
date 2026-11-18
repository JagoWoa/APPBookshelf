package com.example.appbookshelf.model

import com.google.gson.annotations.SerializedName

// Nullable fields also handle incomplete records returned by Google Books.
data class BooksResponse(
    @SerializedName("items") val items: List<BookVolume>? = null,
)

data class BookVolume(
    @SerializedName("id") val id: String? = null,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo? = null,
)

data class VolumeInfo(
    @SerializedName("title") val title: String? = null,
    @SerializedName("authors") val authors: List<String>? = null,
    @SerializedName("imageLinks") val imageLinks: ImageLinks? = null,
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("smallThumbnail") val smallThumbnail: String? = null,
)

data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val thumbnailUrl: String?,
)
