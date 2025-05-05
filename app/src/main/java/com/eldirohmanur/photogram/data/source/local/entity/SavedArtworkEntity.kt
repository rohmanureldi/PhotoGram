package com.eldirohmanur.photogram.data.source.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_artworks")
data class SavedArtworkEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artistName: String?,
    val dateDisplay: String?,
    val mediumDisplay: String?,
    val imageId: String?,
    val description: String?,
    val savedAt: Long = System.currentTimeMillis()
)


