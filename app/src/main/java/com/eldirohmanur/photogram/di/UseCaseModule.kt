package com.eldirohmanur.photogram.di

import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.FetchArtworkDetailUseCase
import com.eldirohmanur.photogram.domain.usecase.FetchArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SaveArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.SearchArtworksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun bindGetArtworkUseCase(
        repo: ArtworkRepo
    ) = FetchArtworksUseCase(repo)

    @Provides
    @Singleton
    fun bindSaveArtworkUseCase(
        repo: SavedArtworkRepo,
        mapper: ArtworkMapperDomain
    ) = SaveArtworkUseCase(repo, mapper)

    @Provides
    @Singleton
    fun bindGetSavedArtworkUseCase(
        repo: SavedArtworkRepo
    ) = GetSavedArtworksUseCase(repo)

    @Provides
    @Singleton
    fun bindDeleteArtworkUseCase(
        repo: SavedArtworkRepo
    ) = DeleteSavedArtworkUseCase(repo)

    @Provides
    @Singleton
    fun bindSearchArtworkUseCase(
        repo: ArtworkRepo
    ) = SearchArtworksUseCase(repo)

    @Provides
    @Singleton
    fun bindGetDetailArtworkUseCase(
        repo: ArtworkRepo,
        mapper: ArtworkMapperDomain
    ) = FetchArtworkDetailUseCase(repo, mapper)
}