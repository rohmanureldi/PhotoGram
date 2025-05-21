package com.eldirohmanur.photogram.presentation.home

import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeScreenState(
    val artworks: ImmutableList<ArtworkUiModel> = persistentListOf(),
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)