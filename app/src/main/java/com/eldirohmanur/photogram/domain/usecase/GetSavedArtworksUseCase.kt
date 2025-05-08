package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedArtworksUseCase @Inject constructor(
    private val repository: SavedArtworkRepo
) {
    operator fun invoke(): Flow<List<ArtworkDomain>> {
        return repository.getSavedArtworks()
    }

    suspend operator fun invoke(id: Int): ArtworkDomain? {
        return repository.getSavedArtworkById(id)
    }
}