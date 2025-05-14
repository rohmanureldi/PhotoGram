package com.eldirohmanur.photogram.domain

import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

data object ArtworkMapperDomain {

    fun toArtworkDomain(
        detailDomain: ArtworkDetailDomain,
        isSaved: Boolean? = null
    ): ArtworkDomain {
        return ArtworkDomain(
            id = detailDomain.id,
            title = detailDomain.title,
            artistName = detailDomain.artistName,
            dateDisplay = detailDomain.dateDisplay,
            mediumDisplay = detailDomain.mediumDisplay,
            imageId = detailDomain.imageId,
            description = detailDomain.description,
            isSaved = isSaved ?: false
        )
    }

    fun toArtworkDomain(uiModel: ArtworkUiModel, isSaved: Boolean) = ArtworkDomain(
        id = uiModel.id,
        title = uiModel.title,
        artistName = uiModel.artist,
        dateDisplay = uiModel.date,
        mediumDisplay = "",
        imageId = uiModel.imageId,
        description = uiModel.description,
        isSaved = isSaved,
        thumbnailUrl = uiModel.thumbnailUrl
    )
}
