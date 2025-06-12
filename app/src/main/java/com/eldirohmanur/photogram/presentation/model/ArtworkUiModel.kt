package com.eldirohmanur.photogram.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class ArtworkUiModel(
    val id: Int = 0,
    val title: String = "",
    val artist: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val description: String = "",
    val imageId: String = "",
    val provenance: String = "",
    val publicationHistory: String = "",
    val exhibitionHistory: String = "",
    val credit: String = "",
    val isSaved: Boolean = false
)
