package com.eldirohmanur.photogram.di

import com.eldirohmanur.photogram.data.mapper.ArtworkMapperData
import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
import com.eldirohmanur.photogram.utils.Dispatch
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
    @Provides
    @Singleton
    fun provideDispatchers() = Dispatch()

    @Provides
    @Singleton
    fun dataMapper() = ArtworkMapperData

    @Provides
    @Singleton
    fun domainMapper() = ArtworkMapperDomain

    @Provides
    @Singleton
    fun uiMapper() = ArtworkMapperUi
}