package com.dott.restaurantexplorer.api.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(
    val id: String,
    val name: String,
    val contact: Contact,
    val location: Location,
    val categories: List<Category>,
    val url: String
) : ClusterItem, Parcelable {

    override fun getPosition() = LatLng(location.lat, location.lng)

    override fun getTitle() = name

    override fun getSnippet(): String? = id + name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Venue

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}