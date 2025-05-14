package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import javax.inject.Inject

class SaveArtworkUseCase @Inject constructor(
    private val repository: SavedArtworkRepo,
    private val mapper: ArtworkMapperDomain
) {
    suspend operator fun invoke(artwork: ArtworkDomain) {
        repository.saveArtwork(artwork)
    }

    suspend operator fun invoke(artworkDetail: ArtworkDetailDomain) {
        val artwork = mapper.toArtworkDomain(artworkDetail, true)
        repository.saveArtwork(artwork)
    }
}