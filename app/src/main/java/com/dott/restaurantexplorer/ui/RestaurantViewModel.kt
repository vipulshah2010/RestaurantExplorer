package com.dott.restaurantexplorer.ui

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dott.restaurantexplorer.R
import com.dott.restaurantexplorer.api.model.ResponseWrapper
import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.dott.restaurantexplorer.repository.RestaurantRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
class RestaurantViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context,
    private val repository: RestaurantRepository
) : ViewModel() {

    public val locationData = LocationLiveData(context)

    private var _venuesLiveData = MutableLiveData<VenueResult<List<Venue>>>()
    private var _selectedVenueLiveData = MutableLiveData<Venue>()

    val venuesLiveData: LiveData<VenueResult<List<Venue>>>
        get() = _venuesLiveData

    val selectedVenueLiveData: LiveData<Venue>
        get() = _selectedVenueLiveData


    /**
     *  Fetch restaurant within map visible radius.
     *
     *  This method only makes api calls, if all criterias are satisfied.
     *
     *  1. Start with showing cached restaurants visible in radius
     *  2. Displayed updated restaurants once api call is successful.
     *  3. In case of error, check error code. if 400 then show server returned error, or locally mapped error.
     */
    fun getVenues(location: LatLng, radius: Double, bounds: LatLngBounds) {
        if (location.latitude != 0.0 &&
            location.longitude != 0.0 &&
            radius <= 1_00_000
        ) {
            viewModelScope.launch {
                repository.getVenues(location, radius, bounds)
                    .onStart {
                        _venuesLiveData.value = repository.getCachedVenues(bounds)
                    }.catch {
                        _venuesLiveData.value = parseError(it)
                    }.collect {
                        _venuesLiveData.value = it
                    }
            }
        }
    }

    /**
     * Parse error and return {@link VenueResult.Error}.
     *
     * if errorCode is 400, then parse error response and return deserialized error message returned by server.
     * if errorCode is not 400, display local error message based on throwable type.
     */
    private fun parseError(throwable: Throwable): VenueResult.Error {
        if (throwable is HttpException && throwable.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
            throwable.response()?.errorBody()?.let { body ->
                val response =
                    Moshi.Builder().build().adapter(ResponseWrapper::class.java)
                        .fromJson(body.string())
                response?.meta?.let {
                    return VenueResult.Error(-1, it.errorDetail)
                }
            }
        }

        return when (throwable) {
            is SocketTimeoutException -> VenueResult.Error(R.string.error_network_timeout)
            is IOException -> VenueResult.Error(R.string.error_offline)
            else -> VenueResult.Error(R.string.error_generic)
        }
    }
}