package com.example.appbookshelf.data

import com.example.appbookshelf.model.Book
import com.example.appbookshelf.model.BookVolume
import com.example.appbookshelf.network.BooksApiService
import com.google.gson.JsonParseException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.HttpException
import java.io.IOException

data class BookSearchResult(val books: List<Book>, val hasIncompleteDetails: Boolean)

interface BooksRepository {
    suspend fun searchBooks(query: String): BookSearchResult
}

class NetworkBooksRepository(private val service: BooksApiService) : BooksRepository {
    // Limit simultaneous detail requests, including across cancelled/new searches.
    private val detailRequests = Semaphore(4)

    override suspend fun searchBooks(query: String): BookSearchResult = coroutineScope {
        val volumes = service.searchBooks(query).items.orEmpty()
            .filter { !it.id.isNullOrBlank() }
            .distinctBy { it.id }

        val results = volumes.map { summary ->
            async {
                detailRequests.withPermit {
                    // Keep the search metadata usable if one detail request fails.
                    val detail = try {
                        service.getBook(requireNotNull(summary.id))
                    } catch (_: IOException) {
                        null
                    } catch (_: HttpException) {
                        null
                    } catch (_: JsonParseException) {
                        null
                    }
                    Pair(toBook(summary, detail), detail?.volumeInfo == null)
                }
            }
        }.awaitAll()

        BookSearchResult(
            books = results.map { it.first },
            hasIncompleteDetails = results.any { it.second },
        )
    }

    private fun toBook(summary: BookVolume, detail: BookVolume?): Book {
        val info = detail?.volumeInfo
        val fallback = summary.volumeInfo
        val thumbnail = info?.imageLinks?.thumbnail
            ?: fallback?.imageLinks?.thumbnail
            ?: info?.imageLinks?.smallThumbnail
            ?: fallback?.imageLinks?.smallThumbnail
        return Book(
            id = requireNotNull(summary.id),
            title = info?.title?.takeIf { it.isNotBlank() } ?: fallback?.title.orEmpty(),
            authors = info?.authors ?: fallback?.authors.orEmpty(),
            thumbnailUrl = thumbnail?.takeIf { it.isNotBlank() }
                ?.replaceFirst("http://", "https://"),
        )
    }
}
