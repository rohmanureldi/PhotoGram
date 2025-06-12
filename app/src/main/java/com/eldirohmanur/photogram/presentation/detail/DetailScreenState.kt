package com.eldirohmanur.photogram.presentation.detail

import androidx.compose.runtime.Stable
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel


@Stable
data class DetailScreenState(
    val artwork: ArtworkUiModel = ArtworkUiModel(),
    val state: DetailScreenContentState = DetailScreenContentState.Loading
)

@Stable
sealed class DetailScreenContentState() {
    data object Loading: DetailScreenContentState()
    data class Error(val msg: String): DetailScreenContentState()
    data object Success: DetailScreenContentState()
}