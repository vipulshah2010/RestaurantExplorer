package com.dott.restaurantexplorer.api

class VenueRecommendationsQueryBuilder : QueryBuilder() {
    private lateinit var latitudeLongitude: String
    private var radius: Double = 0.0

    fun setLatitudeLongitude(
        latitude: Double,
        longitude: Double
    ): VenueRecommendationsQueryBuilder {
        this.latitudeLongitude = "$latitude,$longitude"
        return this
    }

    fun setRadius(
        radius: Double
    ): VenueRecommendationsQueryBuilder {
        this.radius = radius
        return this
    }

    override fun putQueryParams(queryParams: MutableMap<String, String>) {
        queryParams["ll"] = latitudeLongitude
        queryParams["radius"] = "$radius"
    }
}
