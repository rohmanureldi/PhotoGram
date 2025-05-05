package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteSavedArtworkUseCase @Inject constructor(
    private val repository: SavedArtworkRepo
) {
    suspend operator fun invoke(artworkId: Int) {
        withContext(Dispatchers.IO) {
            repository.deleteArtwork(artworkId)
        }
    }
}