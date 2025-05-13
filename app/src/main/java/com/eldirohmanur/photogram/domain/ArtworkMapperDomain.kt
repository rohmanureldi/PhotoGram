package com.eldirohmanur.photogram.domain

import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDataResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailDataResponse
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel

fun SavedArtworkEntity.toArtwork(): ArtworkDomain {
    return ArtworkDomain(
        id = id,
        title = title,
        artistName = artistName,
        dateDisplay = dateDisplay,
        mediumDisplay = mediumDisplay,
        imageId = imageId,
        description = description,
        isSaved = true
    )
}

fun ArtworkDataResponse.toArtwork(): ArtworkDomain {
    return ArtworkDomain(
        id = id,
        title = title,
        artistName = artistName,
        dateDisplay = dateDisplay,
        mediumDisplay = mediumDisplay,
        imageId = imageId,
        description = description,
        isSaved = false,
        thumbnailUrl = thumbnail?.lqip.orEmpty()
    )
}


fun ArtworkDetailDataResponse.toArtworkDetail(): ArtworkDetailDomain {
    return ArtworkDetailDomain(
        id = id ?: 0,
        title = title.orEmpty(),
        artistName = artistName.orEmpty(),
        dateDisplay = dateDisplay.orEmpty(),
        mediumDisplay = mediumDisplay.orEmpty(),
        dimensions = dimensions.orEmpty(),
        imageId = imageId.orEmpty(),
        description = description.orEmpty(),
        provenanceText = provenanceText.orEmpty(),
        exhibitionHistory = exhibitionHistory.orEmpty(),
        publicationHistory = publicationHistory.orEmpty(),
        placeOfOrigin = placeOfOrigin.orEmpty(),
        isSaved = false,
    )
}


fun ArtworkDetailDomain.toArtworkDomain(): ArtworkDomain {
    return ArtworkDomain(
        id = id,
        title = title,
        artistName = artistName,
        dateDisplay = dateDisplay,
        mediumDisplay = mediumDisplay,
        imageId = imageId,
        description = description,
        isSaved = false
    )
}

fun ArtworkUiModel.toArtworkDomain(isSaved: Boolean) = ArtworkDomain(
    id = id,
    title = title,
    artistName = artist,
    dateDisplay = date,
    mediumDisplay = "",
    imageId = imageId,
    description = description,
    isSaved = isSaved,
    thumbnailUrl = thumbnailUrl
)