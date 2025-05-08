package com.eldirohmanur.photogram.di

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
}