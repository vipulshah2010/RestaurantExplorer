package com.dott.restaurantexplorer.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val phone: String,
    val formattedPhone: String,
    val twitter: String,
    val instagram: String,
    val facebook: String,
    val facebookUsername: String,
    val facebookName: String
) : Parcelable
