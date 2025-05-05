package com.eldirohmanur.photogram.domain.repo

import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain

interface ArtworkRepo {
    suspend fun getArtworks(page: Int, limit: Int): Result<List<ArtworkDomain>>
    suspend fun getArtworkDetail(id: Int): Result<ArtworkDetailDomain>
    suspend fun searchArtworks(query: String, page: Int, limit: Int): Result<List<ArtworkDomain>>
}
