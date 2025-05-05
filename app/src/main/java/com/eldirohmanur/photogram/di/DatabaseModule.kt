package com.eldirohmanur.photogram.di

import android.content.Context
import androidx.room.Room
import com.eldirohmanur.photogram.data.source.local.SavedArtworkDao
import com.eldirohmanur.photogram.data.source.local.db.ArtworkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): ArtworkDatabase {
        return Room.databaseBuilder(
            context,
            ArtworkDatabase::class.java,
            "art_gallery_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSavedArtworkDao(database: ArtworkDatabase): SavedArtworkDao {
        return database.savedArtworkDao()
    }
}
