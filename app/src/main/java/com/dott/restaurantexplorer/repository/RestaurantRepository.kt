package com.dott.restaurantexplorer.repository

import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {

    fun getVenues(location: LatLng, radius: Double, bounds: LatLngBounds):
            Flow<VenueResult<List<Venue>>>

    fun getCachedVenues(bounds: LatLngBounds): VenueResult.Success<List<Venue>>
}