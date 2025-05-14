package com.eldirohmanur.photogram.data.repo

import com.eldirohmanur.photogram.data.mapper.ArtworkMapperData
import com.eldirohmanur.photogram.data.source.local.SavedArtworkDao
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import com.eldirohmanur.photogram.utils.Dispatch
import com.eldirohmanur.photogram.utils.mapAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SavedArtworkRepoImpl @Inject constructor(
    private val savedArtworkDao: SavedArtworkDao,
    private val dispatcher: Dispatch,
    private val mapper: ArtworkMapperData
) : SavedArtworkRepo {

    override fun getSavedArtworks(): Flow<List<ArtworkDomain>> {
        return savedArtworkDao.getAllSavedArtworks().map { entities ->
            entities.mapAsync(dispatcher) {
                mapper.toArtwork(it)
            }
        }
    }

    override suspend fun getSavedArtworkById(id: Int): ArtworkDomain? {
        return savedArtworkDao.getSavedArtworkById(id)?.let {
            mapper.toArtwork(it)
        }
    }

    override suspend fun saveArtwork(artwork: ArtworkDomain) {
        val entity = mapper.toArtworkEntity(artwork)
        savedArtworkDao.saveArtwork(entity)
    }

    override suspend fun deleteArtwork(artworkId: Int) {
        savedArtworkDao.deleteArtwork(artworkId)
    }

    override suspend fun isArtworkSaved(artworkId: Int): Boolean {
        return savedArtworkDao.isArtworkSaved(artworkId)
    }
}