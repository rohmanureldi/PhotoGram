package com.eldirohmanur.photogram.presentation.detail

import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel


data class DetailScreenState(
    val artwork: ArtworkUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)