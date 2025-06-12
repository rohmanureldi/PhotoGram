package com.eldirohmanur.photogram.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.FetchArtworkDetailUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SaveArtworkUseCase
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
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
    private val removeSavedArtworkUseCase: DeleteSavedArtworkUseCase,
    private val mapper: ArtworkMapperUi,
    private val domainMapper: ArtworkMapperDomain
) : ViewModel() {

    private val _state = MutableStateFlow(DetailScreenState())
    val state: StateFlow<DetailScreenState> = _state.asStateFlow()

    private val _screenState = MutableStateFlow<DetailScreenContentState>(DetailScreenContentState.Loading)
    val screenState: StateFlow<DetailScreenContentState> = _screenState.asStateFlow()

    fun loadArtwork(artworkId: Int) {
        viewModelScope.launch {
            _screenState.update { DetailScreenContentState.Loading }

            try {
                val response =
                    getArtworkByIdUseCase(artworkId) ?: fetchArtworkDetailUseCase(artworkId)

                response?.let { artwork ->
                    _state.update {
                        it.copy(
                            artwork = mapper.toArtworkUI(artwork),
                        )
                    }
                    _screenState.update {
                        DetailScreenContentState.Success
                    }
                }


            } catch (e: Exception) {
                _screenState.update {
                    DetailScreenContentState.Error("Failed to load artwork: ${e.message}")
                }
            }
        }
    }

    fun toggleSave() {
        val artwork = state.value.artwork ?: return
        if (artwork.isSaved) {
            removeFromSaved(artwork.id)
        } else {
            saveArtwork()
        }
    }

    private fun saveArtwork() {
        val artwork = state.value.artwork ?: return

        viewModelScope.launch {
            try {
                val artworkDomain = domainMapper.toArtworkDomain(artwork, true)
                saveArtworkUseCase(artworkDomain)
                _state.update {
                    it.copy(
                        artwork = artwork.copy(isSaved = true)
                    )
                }
            } catch (e: Exception) {
                _screenState.update {
                    DetailScreenContentState.Error("Failed to save artwork: ${e.message}")
                }
            }
        }
    }

    private fun removeFromSaved(artworkId: Int) {
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
                _screenState.update {
                    DetailScreenContentState.Error("Failed to remove artwork: ${e.message}")
                }
            }
        }
    }
}