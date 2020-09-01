package com.dott.restaurantexplorer.repository

import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {

    /**
     * Fetch restaurants within given radius
     *
     *  @param location map target location
     *  @param radius Radius within which search is performed.
     *  @param bounds map visible region
     */
    fun getVenues(location: LatLng, radius: Double, bounds: LatLngBounds):
            Flow<VenueResult<List<Venue>>>

    /**
     *  @param bounds map visible region
     */
    fun getCachedVenues(bounds: LatLngBounds): Flow<VenueResult<List<Venue>>>
}