package com.eldirohmanur.photogram.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.FetchArtworkDetailUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SaveArtworkUseCase
import com.eldirohmanur.photogram.presentation.mapper.toArtworkUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getArtworkByIdUseCase: GetSavedArtworksUseCase,
    private val fetchArtworkDetailUseCase: FetchArtworkDetailUseCase,
    private val saveArtworkUseCase: SaveArtworkUseCase,
    private val removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailScreenState())
    val state: StateFlow<DetailScreenState> = _state.asStateFlow()

    fun loadArtwork(artworkId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val artwork = getArtworkByIdUseCase(artworkId) ?: fetchArtworkDetailUseCase(artworkId)
                _state.update {
                    it.copy(
                        artwork = artwork?.toArtworkUI(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load artwork: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveArtwork() {
        val artwork = state.value.artwork ?: return

        viewModelScope.launch {
            try {
                saveArtworkUseCase(artwork)
                _state.update {
                    it.copy(
                        artwork = artwork.copy(isSaved = true)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to save artwork: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeFromSaved(artworkId: Int) {
        val artwork = state.value.artwork ?: return

        viewModelScope.launch {
            try {
                removeSavedArtworkUseCase(artworkId)
                _state.update {
                    it.copy(
                        artwork = artwork.copy(isSaved = false)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to remove artwork: ${e.message}"
                    )
                }
            }
        }
    }
}