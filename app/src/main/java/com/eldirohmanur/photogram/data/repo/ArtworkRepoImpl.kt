package com.eldirohmanur.photogram.data.repo

import com.eldirohmanur.photogram.data.source.remote.service.ArtInstituteApi
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import com.eldirohmanur.photogram.domain.toArtwork
import com.eldirohmanur.photogram.domain.toArtworkDetail
import javax.inject.Inject


class ArtworkRepoImpl @Inject constructor(
    private val api: ArtInstituteApi,
    private val savedArtworkRepository: SavedArtworkRepo
) : ArtworkRepo {

    override suspend fun getArtworks(page: Int, limit: Int): Result<List<ArtworkDomain>> {
        return try {
            val response = api.getArtworks(page, limit)
            val artworks = response.data.map { it.toArtwork() }

            // Check if each artwork is saved
            val artworksWithSavedStatus = artworks.map { artwork ->
                artwork.copy(isSaved = savedArtworkRepository.isArtworkSaved(artwork.id))
            }

            Result.success(artworksWithSavedStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtworkDetail(id: Int): Result<ArtworkDetailDomain> {
        return try {
            val response = api.getArtworkDetail(id)
            val isSaved = savedArtworkRepository.isArtworkSaved(id)
            Result.success(response.data.toArtworkDetail().copy(isSaved = isSaved))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchArtworks(
        query: String,
        page: Int,
        limit: Int
    ): Result<List<ArtworkDomain>> {
        return try {
            val response = api.searchArtworks(
                query = query,
                page = page,
                limit = limit
            )
            val artworks = response.data.map { it.toArtwork() }

            // Check if each artwork is saved
            val artworksWithSavedStatus = artworks.map { artwork ->
                artwork.copy(isSaved = savedArtworkRepository.isArtworkSaved(artwork.id))
            }

            Result.success(artworksWithSavedStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}







