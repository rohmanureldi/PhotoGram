package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchArtworksUseCase @Inject constructor(
    private val repository: ArtworkRepo
) {
    suspend operator fun invoke(page: Int, limit: Int = 20): List<ArtworkDomain>? {
        return withContext(Dispatchers.IO) {
            repository.getArtworks(page, limit).getOrNull()
        }
    }
}