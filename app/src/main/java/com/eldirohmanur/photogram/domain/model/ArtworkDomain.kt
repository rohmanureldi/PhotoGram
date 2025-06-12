package com.eldirohmanur.photogram.domain.model

data class ArtworkDomain(
    val id: Int = 0,
    val title: String = "",
    val artistName: String = "",
    val dateDisplay: String = "",
    val mediumDisplay: String = "",
    val imageId: String = "",
    val description: String = "",
    val isSaved: Boolean = false,
    val thumbnailUrl: String = "",
    val provenance: String = "",
    val publicationHistory: String = "",
    val exhibitionHistory: String = "",
    val credit: String = ""
)