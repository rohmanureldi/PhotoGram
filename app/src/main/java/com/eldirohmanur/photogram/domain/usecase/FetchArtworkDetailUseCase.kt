package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import javax.inject.Inject

class FetchArtworkDetailUseCase @Inject constructor(
    private val repository: ArtworkRepo,
    private val mapper: ArtworkMapperDomain
) {
    suspend operator fun invoke(id: Int): ArtworkDomain? {
        return repository.getArtworkDetail(id).getOrNull()?.let { mapper.toArtworkDomain(it) }
    }
}