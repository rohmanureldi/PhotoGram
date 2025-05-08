package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.domain.toArtworkDomain
import javax.inject.Inject

class FetchArtworkDetailUseCase @Inject constructor(
    private val repository: ArtworkRepo,
) {
    suspend operator fun invoke(id: Int): ArtworkDomain? {
        return repository.getArtworkDetail(id).getOrNull()?.toArtworkDomain()
    }
}