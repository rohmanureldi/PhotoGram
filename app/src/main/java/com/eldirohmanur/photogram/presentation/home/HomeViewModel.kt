package com.eldirohmanur.photogram.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldirohmanur.photogram.domain.usecase.FetchArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SearchArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.utils.ArtworkConst
import com.eldirohmanur.photogram.utils.Dispatch
import com.eldirohmanur.photogram.utils.mapAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getArtworksUseCase: FetchArtworksUseCase,
    private val searchArtworksUseCase: SearchArtworksUseCase,
    private val mapper: ArtworkMapperUi,
    private val dispatch: Dispatch
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private var currentPage: Int = 1
    private var currentSearchPage: Int = 1
    private var searchResultIsEmpty = false

    private var searchJob: Job? = null

    init {
        loadArtworks()
    }

    fun loadArtworks(
        pageSize: Int = ArtworkConst.PAGE_LIMIT
    ) {
        if (_state.value.isLoadMore || _state.value.isLoading) return
        if (_state.value.searchQuery.isNotBlank()) {
            searchArtworks()
            return
        }
        viewModelScope.launch {
            val isInitialLoad = currentPage == 1

            _state.update { mState ->
                mState.copy(
                    isLoading = isInitialLoad, error = null,
                    isLoadMore = !isInitialLoad,
                    artworks = emptyList<ArtworkUiModel>().takeIf { isInitialLoad }
                        ?: mState.artworks
                )
            }

            try {
                val response = getArtworksUseCase(
                    page = currentPage,
                    limit = pageSize
                )?.mapAsync(dispatch) {
                    mapper.toArtworkUI(it)
                }

                currentPage++
                _state.update {
                    it.copy(
                        artworks = it.artworks + response.orEmpty(),
                        isLoading = false,
                        isLoadMore = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load artworks: ${e.message}",
                        isLoadMore = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchResultIsEmpty = false
        searchJob?.cancel()

        resetCurrentPage()
        if (query.isEmpty()) {
            resetCurrentSearchPage()
            loadArtworks()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            searchArtworks()
        }
    }

    fun searchArtworks() {
        val query = state.value.searchQuery
        if (_state.value.isLoadMore || _state.value.isLoading || searchResultIsEmpty) return
        if (query.isEmpty()) return
        val isInitialLoad = currentSearchPage == 1

        viewModelScope.launch {
            _state.update { mState ->
                mState.copy(
                    isLoading = isInitialLoad,
                    error = null,
                    isLoadMore = !isInitialLoad,
                    artworks = emptyList<ArtworkUiModel>().takeIf { isInitialLoad }
                        ?: mState.artworks
                )
            }

            try {
                val searchResults = searchArtworksUseCase(
                    query = query,
                    page = currentSearchPage
                ).map {
                    it.mapAsync(dispatch) {
                        mapper.toArtworkUI(it)
                    }
                }.getOrNull()
                currentSearchPage++
                searchResultIsEmpty = searchResults.isNullOrEmpty()
                _state.update {
                    it.copy(
                        artworks = it.artworks + searchResults.orEmpty(),
                        isLoading = false,
                        isLoadMore = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoadMore = false,
                        error = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        resetCurrentPage()
        resetCurrentSearchPage()
        _state.update { it.copy(searchQuery = "") }
        loadArtworks()
    }

    private fun resetCurrentPage() {
        currentPage = 1
    }

    private fun resetCurrentSearchPage() {
        currentSearchPage = 1
    }
}

