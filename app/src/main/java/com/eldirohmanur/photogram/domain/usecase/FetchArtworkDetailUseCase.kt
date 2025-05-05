package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.domain.toArtworkDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchArtworkDetailUseCase @Inject constructor(
    private val repository: ArtworkRepo
) {
    suspend operator fun invoke(id: Int): ArtworkDomain? {
        return withContext(Dispatchers.IO) {
            repository.getArtworkDetail(id).getOrNull()?.toArtworkDomain()
        }
    }
}