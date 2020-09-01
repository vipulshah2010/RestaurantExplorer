package com.dott.restaurantexplorer.api.model

sealed class VenueResult<out T> {

    data class Success<T>(val data: T) : VenueResult<T>()
    data class Error(val code: Int, val message: String? = null) : VenueResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$code]"
        }
    }
}