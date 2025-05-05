package com.eldirohmanur.photogram.presentation

data class ArtworkUiModel(
    val id: Int,
    val title: String,
    val artist: String,
    val date: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val description: String,
    val imageId: String,
    val isSaved: Boolean = false
)
