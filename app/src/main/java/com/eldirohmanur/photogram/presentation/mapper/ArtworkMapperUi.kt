package com.eldirohmanur.photogram.presentation.mapper

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

data object ArtworkMapperUi {
    fun toArtworkUI(domain: ArtworkDomain): ArtworkUiModel {
        return ArtworkUiModel(
            id = domain.id,
            title = domain.title,
            artist = domain.artistName,
            date = domain.dateDisplay,
            imageUrl = "https://www.artic.edu/iiif/2/${domain.imageId}/full/843,/0/default.jpg",
            description = domain.description,
            isSaved = domain.isSaved,
            imageId = domain.imageId,
            thumbnailUrl = domain.thumbnailUrl,
            provenance = domain.provenance,
            exhibitionHistory = domain.exhibitionHistory,
            publicationHistory = domain.publicationHistory,
            credit = domain.credit
        )
    }
}
