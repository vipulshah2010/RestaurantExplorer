package com.dott.restaurantexplorer

import com.dott.restaurantexplorer.api.model.VenueResult
import com.dott.restaurantexplorer.repository.RestaurantRepository
import com.dott.restaurantexplorer.ui.LocationLiveData
import com.dott.restaurantexplorer.ui.RestaurantViewModel
import com.dott.restaurantexplorer.utils.MockFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.common.truth.Truth.assertThat
import com.vipul.dottrestaurants.utils.CoroutineTest
import com.vipul.dottrestaurants.utils.InstantTaskExecutorExtension
import com.vipul.dottrestaurants.utils.observeForTesting
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class)
internal class RestaurantViewModelTest : CoroutineTest {

    override lateinit var dispatcher: TestCoroutineDispatcher
    override lateinit var testScope: TestCoroutineScope

    private val repository: RestaurantRepository = mockk()
    private val locationLiveData: LocationLiveData = mockk(relaxed = true)

    @Test
    fun `Test loading venues returns correct cached & non-cached results`() =
        dispatcher.runBlockingTest {

            // mock non-cached venues
            every {
                repository.getVenues(
                    location = MockFactory.latLng1,
                    radius = any(),
                    bounds = any()
                )
            } returns flow {
                emit(MockFactory.generateVenues(MockFactory.latLng1, exception = false))
            }

            // mock cached venues
            every {
                repository.getCachedVenues(
                    bounds = any()
                )
            } returns flow {
                emit(MockFactory.generateCachedVenues())
            }

            val viewModel = RestaurantViewModel(locationLiveData, repository)

            viewModel.venuesLiveData.observeForTesting {
                viewModel.getVenues(
                    MockFactory.latLng1,
                    1.0,
                    LatLngBounds(MockFactory.latLng1, MockFactory.latLng2)
                )
                assertThat(it.values).hasSize(2)

                assertThat(it.values[0]).isInstanceOf(VenueResult.Success::class.java)
                assertThat((it.values[0] as VenueResult.Success).data).hasSize(1)
                assertThat((it.values[0] as VenueResult.Success).data[0].id).isEqualTo("11")

                assertThat(it.values[1]).isInstanceOf(VenueResult.Success::class.java)
                assertThat((it.values[1] as VenueResult.Success).data).hasSize(2)
                assertThat((it.values[1] as VenueResult.Success).data[0].id).isEqualTo("1")
                assertThat((it.values[1] as VenueResult.Success).data[1].id).isEqualTo("2")
            }
        }

    @Test
    fun `Test loading venues - Failure`() = dispatcher.runBlockingTest {
        // mock non-cached venues
        every {
            repository.getVenues(
                location = MockFactory.latLng1,
                radius = any(),
                bounds = any()
            )
        } returns flow {
            emit(MockFactory.generateVenues(MockFactory.latLng1, exception = true))
        }

        // mock cached venues
        every {
            repository.getCachedVenues(
                bounds = any()
            )
        } returns flow {
            emit(MockFactory.generateCachedVenues())
        }

        val viewModel = RestaurantViewModel(locationLiveData, repository)

        viewModel.venuesLiveData.observeForTesting {
            viewModel.getVenues(
                MockFactory.latLng1,
                1.0,
                LatLngBounds(MockFactory.latLng1, MockFactory.latLng2)
            )
            assertThat(it.values).hasSize(2)

            assertThat(it.values[0]).isInstanceOf(VenueResult.Success::class.java)
            assertThat((it.values[0] as VenueResult.Success).data).hasSize(1)
            assertThat((it.values[0] as VenueResult.Success).data[0].id).isEqualTo("11")

            assertThat(it.values[1]).isInstanceOf(VenueResult.Error::class.java)
        }
    }

    @Test
    fun `Error is parsed correctly`() {

        val viewModel = RestaurantViewModel(mockk(relaxed = true), repository)

        val throwable = mockk<HttpException>()
        every { throwable.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every {
            throwable.response()?.errorBody()?.string()
        } returns "{\"meta\":{\"errorDetail\":\"Some Error!\"}}"

        assertThat(viewModel.parseError(throwable).message).isEqualTo("Some Error!")
        assertThat(viewModel.parseError(SocketTimeoutException()).code).isEqualTo(R.string.error_network_timeout)
        assertThat(viewModel.parseError(IOException()).code).isEqualTo(R.string.error_offline)
        assertThat(viewModel.parseError(IllegalAccessException()).code).isEqualTo(R.string.error_generic)
    }
}