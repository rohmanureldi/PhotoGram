package com.eldirohmanur.photogram.presentation.home

import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

data class HomeScreenState(
    val artworks: List<ArtworkUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)