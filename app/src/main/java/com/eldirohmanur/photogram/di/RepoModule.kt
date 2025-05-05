package com.eldirohmanur.photogram.di

import com.eldirohmanur.photogram.data.repo.ArtworkRepoImpl
import com.eldirohmanur.photogram.data.repo.SavedArtworkRepoImpl
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindArtworkRepository(
        artworkRepositoryImpl: ArtworkRepoImpl
    ): ArtworkRepo

    @Binds
    @Singleton
    abstract fun bindSavedArtworkRepository(
        savedArtworkRepositoryImpl: SavedArtworkRepoImpl
    ): SavedArtworkRepo

}