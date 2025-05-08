package com.eldirohmanur.photogram.data.repo

import com.eldirohmanur.photogram.data.source.local.SavedArtworkDao
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import com.eldirohmanur.photogram.domain.toArtwork
import com.eldirohmanur.photogram.utils.Dispatch
import com.eldirohmanur.photogram.utils.mapAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SavedArtworkRepoImpl @Inject constructor(
    private val savedArtworkDao: SavedArtworkDao,
    private val dispatcher: Dispatch,
) : SavedArtworkRepo {

    override suspend fun getSavedArtworks(): Flow<List<ArtworkDomain>> {
        return savedArtworkDao.getAllSavedArtworks().map { entities ->
            entities.mapAsync(dispatcher) {
                it.toArtwork()
            }
        }
    }

    override suspend fun getSavedArtworkById(id: Int): ArtworkDomain? {
        return savedArtworkDao.getSavedArtworkById(id)?.toArtwork()
    }

    override suspend fun saveArtwork(artwork: ArtworkDomain) {
        val entity = SavedArtworkEntity(
            id = artwork.id,
            title = artwork.title,
            artistName = artwork.artistName,
            dateDisplay = artwork.dateDisplay,
            mediumDisplay = artwork.mediumDisplay,
            imageId = artwork.imageId,
            description = artwork.description,
            savedAt = System.currentTimeMillis()
        )
        savedArtworkDao.saveArtwork(entity)
    }

    override suspend fun deleteArtwork(artworkId: Int) {
        savedArtworkDao.deleteArtwork(artworkId)
    }

    override suspend fun isArtworkSaved(artworkId: Int): Boolean {
        return savedArtworkDao.isArtworkSaved(artworkId)
    }
}