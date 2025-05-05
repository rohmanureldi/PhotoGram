package com.eldirohmanur.photogram.data.source.local


import androidx.room.*
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArtworkDao {
    @Query("SELECT * FROM saved_artworks ORDER BY savedAt DESC")
    fun getAllSavedArtworks(): Flow<List<SavedArtworkEntity>>

    @Query("SELECT * FROM saved_artworks WHERE id = :id")
    suspend fun getSavedArtworkById(id: Int): SavedArtworkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArtwork(artwork: SavedArtworkEntity)

    @Query("DELETE FROM saved_artworks WHERE id = :artworkId")
    suspend fun deleteArtwork(artworkId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_artworks WHERE id = :artworkId)")
    suspend fun isArtworkSaved(artworkId: Int): Boolean
}
