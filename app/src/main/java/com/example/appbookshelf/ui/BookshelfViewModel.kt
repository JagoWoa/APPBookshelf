package com.example.appbookshelf.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appbookshelf.BookshelfApplication
import com.example.appbookshelf.data.BooksRepository
import com.example.appbookshelf.model.Book
import com.google.gson.JsonParseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(
        val books: List<Book>,
        val query: String,
        val hasIncompleteDetails: Boolean,
    ) : BookshelfUiState
    data class Error(val reason: BookshelfError) : BookshelfUiState
}

enum class BookshelfError { CONNECTION, LIMIT, SERVICE }

class BookshelfViewModel(
    private val repository: BooksRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val searchQuery = savedStateHandle.getStateFlow("searchQuery", DEFAULT_QUERY)
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null

    init {
        loadBooks(savedStateHandle["submittedQuery"] ?: DEFAULT_QUERY)
    }

    fun updateQuery(query: String) {
        savedStateHandle["searchQuery"] = query
    }

    fun search() {
        val query = searchQuery.value.trim()
        if (query.isNotEmpty()) loadBooks(query)
    }

    fun retry() {
        loadBooks(savedStateHandle["submittedQuery"] ?: DEFAULT_QUERY)
    }

    private fun loadBooks(query: String) {
        searchJob?.cancel()
        savedStateHandle["submittedQuery"] = query
        _uiState.value = BookshelfUiState.Loading
        searchJob = viewModelScope.launch {
            _uiState.value = try {
                val result = repository.searchBooks(query)
                BookshelfUiState.Success(result.books, query, result.hasIncompleteDetails)
            } catch (_: IOException) {
                BookshelfUiState.Error(BookshelfError.CONNECTION)
            } catch (exception: HttpException) {
                BookshelfUiState.Error(
                    if (exception.code() == 429 || exception.code() == 403) {
                        BookshelfError.LIMIT
                    } else {
                        BookshelfError.SERVICE
                    }
                )
            } catch (_: JsonParseException) {
                BookshelfUiState.Error(BookshelfError.SERVICE)
            }
        }
    }

    companion object {
        private const val DEFAULT_QUERY = "programación"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as BookshelfApplication
                BookshelfViewModel(application.container.booksRepository, createSavedStateHandle())
            }
        }
    }
}
