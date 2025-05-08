package com.eldirohmanur.photogram.domain.repo

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import kotlinx.coroutines.flow.Flow


interface SavedArtworkRepo {
    fun getSavedArtworks(): Flow<List<ArtworkDomain>>
    suspend fun getSavedArtworkById(id: Int): ArtworkDomain?
    suspend fun saveArtwork(artwork: ArtworkDomain)
    suspend fun deleteArtwork(artworkId: Int)
    suspend fun isArtworkSaved(artworkId: Int): Boolean
}