package com.eldirohmanur.photogram.domain.model


data class ArtworkDomain(
    val id: Int,
    val title: String,
    val artistName: String?,
    val dateDisplay: String?,
    val mediumDisplay: String?,
    val imageId: String?,
    val description: String?,
    val isSaved: Boolean = false,
    val thumbnailUrl: String = ""
)