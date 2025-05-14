package com.eldirohmanur.photogram.presentation.mapper

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

data object ArtworkMapperUi {
    fun toArtworkUI(domain: ArtworkDomain): ArtworkUiModel {
        return ArtworkUiModel(
            id = domain.id,
            title = domain.title,
            artist = domain.artistName.orEmpty(),
            date = domain.dateDisplay.orEmpty(),
            imageUrl = "https://www.artic.edu/iiif/2/${domain.imageId}/full/843,/0/default.jpg",
            description = domain.description.orEmpty(),
            isSaved = domain.isSaved,
            imageId = domain.imageId.orEmpty(),
            thumbnailUrl = domain.thumbnailUrl
        )
    }
}
