package com.eldirohmanur.photogram.data.source.local.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.eldirohmanur.photogram.data.source.local.SavedArtworkDao
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity

@Database(
    entities = [SavedArtworkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ArtworkDatabase : RoomDatabase() {
    abstract fun savedArtworkDao(): SavedArtworkDao
}