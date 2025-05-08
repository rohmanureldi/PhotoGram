package com.eldirohmanur.photogram.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.toArtworkUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SavedImagesViewModel @Inject constructor(
    private val getSavedArtworksUseCase: GetSavedArtworksUseCase,
    private val removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SavedScreenState())
    val state: StateFlow<SavedScreenState> = _state.asStateFlow()

    init {
        loadSavedArtworks()
    }

    private fun loadSavedArtworks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                getSavedArtworksUseCase().map {
                    it.map {
                        it.toArtworkUI()
                    }
                }.collect { artworks ->
                    _state.update {
                        it.copy(
                            savedArtworks = artworks,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun removeFromSaved(artworkId: Int) {
        viewModelScope.launch {
            try {
                removeSavedArtworkUseCase(artworkId)
                // The Flow from getSavedArtworksUseCase will automatically emit new list
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}