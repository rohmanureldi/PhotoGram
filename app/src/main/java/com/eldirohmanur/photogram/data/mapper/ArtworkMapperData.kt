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
        savedAt = System.currentTimeMillis(),
        provenance = domain.provenance,
        publicationHistory = domain.publicationHistory,
        exhibitionHistory = domain.exhibitionHistory,
        credit = domain.credit
    )

    fun toArtwork(entity: SavedArtworkEntity): ArtworkDomain {
        return ArtworkDomain(
            id = entity.id,
            title = entity.title,
            artistName = entity.artistName.orEmpty(),
            dateDisplay = entity.dateDisplay.orEmpty(),
            mediumDisplay = entity.mediumDisplay.orEmpty(),
            imageId = entity.imageId.orEmpty(),
            description = entity.description.orEmpty(),
            isSaved = true,
            provenance = entity.provenance.orEmpty(),
            publicationHistory = entity.publicationHistory.orEmpty(),
            exhibitionHistory = entity.exhibitionHistory.orEmpty(),
            credit = entity.credit.orEmpty()
        )
    }

    fun toArtwork(response: ArtworkDataResponse, isSaved: Boolean? = null): ArtworkDomain {
        return ArtworkDomain(
            id = response.id,
            title = response.title,
            artistName = response.artistName.orEmpty(),
            dateDisplay = response.dateDisplay.orEmpty(),
            mediumDisplay = response.mediumDisplay.orEmpty(),
            imageId = response.imageId.orEmpty(),
            description = response.description.orEmpty(),
            isSaved = isSaved ?: false,
            thumbnailUrl = response.thumbnail?.lqip.orEmpty(),
            provenance = response.provenance.orEmpty(),
            credit = response.credit.orEmpty()
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
            credit = response.credit.orEmpty(),
            isSaved = false,
        )
    }
}
