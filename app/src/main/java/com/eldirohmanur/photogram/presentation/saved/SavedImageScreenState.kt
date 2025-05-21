package com.eldirohmanur.photogram.presentation.saved

import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SavedScreenState(
    val savedArtworks: ImmutableList<ArtworkUiModel> = persistentListOf(),
    val isLoading: Boolean = true
)