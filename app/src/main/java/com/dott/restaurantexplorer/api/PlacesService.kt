package com.dott.restaurantexplorer.api

import com.dott.restaurantexplorer.api.model.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface PlacesService {
    /**
     * Search venues.
     *
     * See [the docs](https://developer.foursquare.com/docs/api-reference/venues/search/)
     */
    @GET("venues/search")
    suspend fun searchVenues(@QueryMap query: Map<String, String>): ResponseWrapper
}
