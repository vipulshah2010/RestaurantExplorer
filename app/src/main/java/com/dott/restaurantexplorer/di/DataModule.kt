package com.dott.restaurantexplorer.di

import com.dott.restaurantexplorer.BuildConfig
import com.dott.restaurantexplorer.api.PlacesService
import com.dott.restaurantexplorer.repository.RestaurantRepository
import com.dott.restaurantexplorer.repository.RestaurantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindRepository(venueRepositoryImpl: RestaurantRepositoryImpl): RestaurantRepository

    companion object {

        @Singleton
        @Provides
        fun provideService(): PlacesService {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.FOURSQUARE_BASE_URL)
                .client(OkHttpClient())
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(PlacesService::class.java)
        }

        @Provides
        fun provideDispatcher() = Dispatchers.IO
    }
}