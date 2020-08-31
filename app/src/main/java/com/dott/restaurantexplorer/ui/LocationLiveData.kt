package com.dott.restaurantexplorer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * Lifecycle aware Location class, which auto registers & deregisters location callback.
 *
 * Results won't be delivered if calling module, is not active.
 */
@SuppressLint("MissingPermission")
class LocationLiveData(context: Context) : LiveData<Location?>() {

    private var currentLocation: Location? = null
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun onActive() {
        super.onActive()

        currentLocation?.let {
            value = it
        } ?: run {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                value = null
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.also {
                        value = it
                    }
                }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let {
                for (location in it.locations) {
                    currentLocation = location
                    value = location
                    fusedLocationClient.removeLocationUpdates(this)
                }
            } ?: run {
                value = null
            }
        }
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}