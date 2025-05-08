package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import com.eldirohmanur.photogram.domain.toArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import javax.inject.Inject

class SaveArtworkUseCase @Inject constructor(
    private val repository: SavedArtworkRepo
) {
    suspend operator fun invoke(artwork: ArtworkDomain) {
        repository.saveArtwork(artwork)
    }

    suspend operator fun invoke(artworkDetail: ArtworkDetailDomain) {
        val artwork = ArtworkDomain(
            id = artworkDetail.id,
            title = artworkDetail.title,
            artistName = artworkDetail.artistName,
            dateDisplay = artworkDetail.dateDisplay,
            mediumDisplay = artworkDetail.mediumDisplay,
            imageId = artworkDetail.imageId,
            description = artworkDetail.description,
            isSaved = true
        )
        repository.saveArtwork(artwork)
    }

    suspend operator fun invoke(artworkUI: ArtworkUiModel) {
        val artwork = artworkUI.toArtworkDomain(true)
        repository.saveArtwork(artwork)
    }
}