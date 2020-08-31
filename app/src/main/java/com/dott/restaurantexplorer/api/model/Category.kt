package com.dott.restaurantexplorer.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val icon: Icon,
    val id: String,
    val name: String,
    val pluralName: String,
    val primary: Boolean,
    val shortName: String
):Parcelable
