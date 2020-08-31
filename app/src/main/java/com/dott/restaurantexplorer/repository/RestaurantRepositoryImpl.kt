package com.dott.restaurantexplorer.repository

import com.dott.restaurantexplorer.api.PlacesService
import com.dott.restaurantexplorer.api.VenueRecommendationsQueryBuilder
import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject
constructor(
    private val service: PlacesService,
    private val dispatcher: CoroutineDispatcher
) : RestaurantRepository {

    private var venues = mutableSetOf<Venue>()

    override fun getVenues(location: LatLng, radius: Double, bounds: LatLngBounds):
            Flow<VenueResult<List<Venue>>> {

        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(location.latitude, location.longitude)
            .setRadius(radius)
            .build()

        return flow {
            val response = service.searchVenues(query)
            emit(response)
        }.map {
            venues.addAll(it.response.venues)
            VenueResult.Success(venues.filter { venue ->
                bounds.contains(
                    LatLng(
                        venue.location.lat,
                        venue.location.lng
                    )
                )
            })
        }.flowOn(dispatcher)
    }

    override fun getCachedVenues(bounds: LatLngBounds): Flow<VenueResult<List<Venue>>> {
        return flow {
            emit(VenueResult.Success(venues.filter { venue ->
                bounds.contains(
                    LatLng(
                        venue.location.lat,
                        venue.location.lng
                    )
                )
            }))
        }.flowOn(dispatcher)
    }
}