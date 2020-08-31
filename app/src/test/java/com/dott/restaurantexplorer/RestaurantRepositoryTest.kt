package com.dott.restaurantexplorer

import com.dott.restaurantexplorer.api.PlacesService
import com.dott.restaurantexplorer.api.model.VenueResult
import com.dott.restaurantexplorer.repository.RestaurantRepositoryImpl
import com.dott.restaurantexplorer.utils.MockFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.common.truth.Truth
import com.vipul.dottrestaurants.utils.CoroutineTest
import com.vipul.dottrestaurants.utils.InstantTaskExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class)
internal class RestaurantRepositoryTest : CoroutineTest {

    override lateinit var dispatcher: TestCoroutineDispatcher
    override lateinit var testScope: TestCoroutineScope

    private val placesService: PlacesService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.FOURSQUARE_BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(PlacesService::class.java)
    }

    /**
     * Currently there is bug with dispatcher.runBlockingTest. it throws error as mentioned here, so using runBlocking.
     * @see <a href="https://github.com/Kotlin/kotlinx.coroutines/issues/1204">Here</a>
     */

    @Test
    fun `valid search returns success`() = runBlocking {
        val repository = RestaurantRepositoryImpl(placesService, dispatcher)

        repository.getVenues(
            MockFactory.latLng1,
            1334.30,
            bounds = LatLngBounds(
                LatLng(52.31489868618412, 4.947963505983352),
                LatLng(52.33644288021378, 4.965158812701702)
            )
        ).collect {
            Truth.assertThat(it).isInstanceOf(VenueResult.Success::class.java)
        }
    }

    @Test
    fun `cached results returned if cached data exists for given bounds`() = runBlocking {
        val repository = RestaurantRepositoryImpl(placesService, dispatcher)

        repository.getVenues(
            MockFactory.latLng1,
            1334.30,
            bounds = LatLngBounds(
                LatLng(52.31489868618412, 4.947963505983352),
                LatLng(52.33644288021378, 4.965158812701702)
            )
        ).collect {
            Truth.assertThat(it).isInstanceOf(VenueResult.Success::class.java)

            // Trying to look for cached data within fetched region
            repository.getCachedVenues(
                bounds = LatLngBounds(
                    LatLng(52.31489868618412, 4.947963505983352),
                    LatLng(52.33644288021378, 4.965158812701702)
                )
            ).collect { cachedResult ->
                Truth.assertThat(cachedResult).isInstanceOf(VenueResult.Success::class.java)
                Truth.assertThat((cachedResult as VenueResult.Success).data).isNotEmpty()
            }
        }
    }

    @Test
    fun `empty results returned if cached data does not exists for given bounds`() = runBlocking {
        val repository = RestaurantRepositoryImpl(placesService, dispatcher)

        repository.getVenues(
            MockFactory.latLng1,
            1334.30,
            bounds = LatLngBounds(
                LatLng(52.31489868618412, 4.947963505983352),
                LatLng(52.33644288021378, 4.965158812701702)
            )
        ).collect {
            Truth.assertThat(it).isInstanceOf(VenueResult.Success::class.java)

            // Trying to look for cached data outside fetched region
            repository.getCachedVenues(
                bounds = LatLngBounds(
                    LatLng(62.31489868618412, 4.947963505983352),
                    LatLng(72.33644288021378, 4.965158812701702)
                )
            ).collect { cachedResult ->
                Truth.assertThat(cachedResult).isInstanceOf(VenueResult.Success::class.java)
                Truth.assertThat((cachedResult as VenueResult.Success).data).isEmpty()
            }
        }
    }
}