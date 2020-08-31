package com.dott.restaurantexplorer.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val formattedAddress: List<String>,
    val lat: Double,
    val lng: Double,
) : Parcelable
