package com.dott.restaurantexplorer.di

import android.content.Context
import com.dott.restaurantexplorer.ui.LocationLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class UIModule {

    companion object {

        @Singleton
        @Provides
        fun provideLocationLive(@ApplicationContext context: Context) = LocationLiveData(context)
    }
}