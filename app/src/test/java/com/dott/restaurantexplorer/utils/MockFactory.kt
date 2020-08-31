package com.dott.restaurantexplorer.utils

import androidx.annotation.VisibleForTesting
import com.dott.restaurantexplorer.api.model.Location
import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.google.android.gms.maps.model.LatLng

object MockFactory {

    val latLng1 = LatLng(52.34376382455167, 4.954437017440796)
    val latLng2 = LatLng(52.413036092610575, 4.948199205100537)

    private val venuesMap = HashMap<LatLng, ArrayList<Venue>>().apply {
        put(latLng1, arrayListOf<Venue>().apply {
            add(
                Venue(
                    "1",
                    "Venue 1",
                    Location(arrayListOf(), 52.35459372072252, 4.953464396906372),
                    arrayListOf(),
                    ""
                )
            )
            add(
                Venue(
                    "2",
                    "Venue 2",
                    Location(arrayListOf(), 52.33839, 4.960113),
                    arrayListOf(),
                    ""
                )
            )
        })

        put(latLng2, arrayListOf<Venue>().apply {
            add(
                Venue(
                    "3",
                    "Venue 1",
                    Location(arrayListOf(), 52.39771473832792, 4.937933333866097),
                    arrayListOf(),
                    ""
                )
            )
            add(
                Venue(
                    "4",
                    "Venue 2",
                    Location(arrayListOf(), 52.392323, 4.9539466),
                    arrayListOf(),
                    ""
                )
            )
        })
    }

    private val cachedVenuesMap = HashMap<LatLng, ArrayList<Venue>>().apply {
        put(latLng1, arrayListOf<Venue>().apply {
            add(
                Venue(
                    "11",
                    "Venue 1",
                    Location(arrayListOf(), 52.35459372072252, 4.953464396906372),
                    arrayListOf(),
                    ""
                )
            )
        })
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun generateVenues(latLng: LatLng, exception: Boolean) = when {
        exception -> VenueResult.Error(-1)
        else -> VenueResult.Success(venuesMap[latLng]!!)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun generateCachedVenues() =
        VenueResult.Success(cachedVenuesMap[latLng1]!!)
}