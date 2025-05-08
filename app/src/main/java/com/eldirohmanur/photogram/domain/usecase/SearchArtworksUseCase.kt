package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.utils.ArtworkConst
import javax.inject.Inject


class SearchArtworksUseCase @Inject constructor(
    private val repository: ArtworkRepo
) {
    suspend operator fun invoke(query: String, page: Int = 1, limit: Int = ArtworkConst.PAGE_LIMIT): Result<List<ArtworkDomain>> {
        return repository.searchArtworks(query, page, limit)
    }
}