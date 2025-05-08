package com.eldirohmanur.photogram.presentation.saved

import com.eldirohmanur.photogram.presentation.ArtworkUiModel

data class SavedScreenState(
    val savedArtworks: List<ArtworkUiModel> = emptyList(),
    val isLoading: Boolean = false
)