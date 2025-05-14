package com.eldirohmanur.photogram.data.mapper

import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDataResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailDataResponse
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain

data object ArtworkMapperData {
    fun toArtworkEntity(domain: ArtworkDomain) = SavedArtworkEntity(
        id = domain.id,
        title = domain.title,
        artistName = domain.artistName,
        dateDisplay = domain.dateDisplay,
        mediumDisplay = domain.mediumDisplay,
        imageId = domain.imageId,
        description = domain.description,
        savedAt = System.currentTimeMillis()
    )

    fun toArtwork(entity: SavedArtworkEntity): ArtworkDomain {
        return ArtworkDomain(
            id = entity.id,
            title = entity.title,
            artistName = entity.artistName,
            dateDisplay = entity.dateDisplay,
            mediumDisplay = entity.mediumDisplay,
            imageId = entity.imageId,
            description = entity.description,
            isSaved = true
        )
    }

    fun toArtwork(response: ArtworkDataResponse, isSaved: Boolean? = null): ArtworkDomain {
        return ArtworkDomain(
            id = response.id,
            title = response.title,
            artistName = response.artistName,
            dateDisplay = response.dateDisplay,
            mediumDisplay = response.mediumDisplay,
            imageId = response.imageId,
            description = response.description,
            isSaved = isSaved ?: false,
            thumbnailUrl = response.thumbnail?.lqip.orEmpty()
        )
    }

    fun toArtworkDetail(response: ArtworkDetailDataResponse): ArtworkDetailDomain {
        return ArtworkDetailDomain(
            id = response.id ?: 0,
            title = response.title.orEmpty(),
            artistName = response.artistName.orEmpty(),
            dateDisplay = response.dateDisplay.orEmpty(),
            mediumDisplay = response.mediumDisplay.orEmpty(),
            dimensions = response.dimensions.orEmpty(),
            imageId = response.imageId.orEmpty(),
            description = response.description.orEmpty(),
            provenanceText = response.provenanceText.orEmpty(),
            exhibitionHistory = response.exhibitionHistory.orEmpty(),
            publicationHistory = response.publicationHistory.orEmpty(),
            placeOfOrigin = response.placeOfOrigin.orEmpty(),
            isSaved = false,
        )
    }
}
