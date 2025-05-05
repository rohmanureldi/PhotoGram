package com.eldirohmanur.photogram.domain.model

data class ArtworkDetailDomain(
    val id: Int = 0,
    val title: String = "",
    val artistName: String = "",
    val dateDisplay: String = "",
    val mediumDisplay: String = "",
    val dimensions: String = "",
    val imageId: String = "",
    val description: String = "",
    val provenanceText: String = "",
    val exhibitionHistory: String = "",
    val publicationHistory: String = "",
    val placeOfOrigin: String = "",
    val isSaved: Boolean = false
)