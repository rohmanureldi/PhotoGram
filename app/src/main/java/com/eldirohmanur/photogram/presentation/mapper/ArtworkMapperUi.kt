package com.eldirohmanur.photogram.presentation.mapper

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

fun ArtworkDomain.toArtworkUI(): ArtworkUiModel {
    return ArtworkUiModel(
        id = id,
        title = title,
        artist = artistName.orEmpty(),
        date = dateDisplay.orEmpty(),
        imageUrl = "https://www.artic.edu/iiif/2/$imageId/full/843,/0/default.jpg",
        description = description.orEmpty(),
        isSaved = isSaved,
        imageId = imageId.orEmpty(),
        thumbnailUrl = this.thumbnailUrl
    )
}