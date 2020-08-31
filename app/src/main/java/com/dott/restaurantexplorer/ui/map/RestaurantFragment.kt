package com.dott.restaurantexplorer.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dott.restaurantexplorer.PermissionCallback
import com.dott.restaurantexplorer.PermissionObserver
import com.dott.restaurantexplorer.R
import com.dott.restaurantexplorer.api.model.VenueResult
import com.dott.restaurantexplorer.databinding.FragmentRestaurantBinding
import com.dott.restaurantexplorer.ui.RestaurantViewModel
import com.dott.restaurantexplorer.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.CameraIdleEvent
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
// Handling runtime permission, so skipping this lint warning.
@SuppressLint("MissingPermission")
@AndroidEntryPoint
class RestaurantFragment : BaseFragment<FragmentRestaurantBinding>(), PermissionCallback {

    private lateinit var permissionObserver: PermissionObserver
    private var googleMap: GoogleMap? = null
    private lateinit var map: SupportMapFragment

    private val viewModel: RestaurantViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.isVisible = false

        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        permissionObserver =
            PermissionObserver(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION, this
            )

        lifecycle.addObserver(permissionObserver)

        viewModel.venuesLiveData.observe(requireActivity(), {
            when (it) {
                VenueResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is VenueResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        it.message ?: requireContext().getString(it.code),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.fab).show()
                }
                is VenueResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Display restaurants
                }
            }
        })

        viewModel.selectedVenueLiveData.observe(requireActivity(), {
            // display details
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            googleMap = map.awaitMap()

            googleMap?.let {
                binding.fab.isVisible = true
                it.uiSettings.isZoomControlsEnabled = true
                permissionObserver.requestPermission()

                it.cameraEvents().collect { event ->

                    when (event) {
                        is CameraIdleEvent -> {
                            it.cameraPosition?.target?.let { target ->
                                it.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                                    viewModel.getVenues(target, 0.0, bounds)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.fab.setOnClickListener {
            permissionObserver.requestPermission()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ) = FragmentRestaurantBinding.inflate(inflater, container, false)

    override fun onPermissionGranted() {
        viewModel.locationData.observe(this, {
            it?.let {
                googleMap?.run {
                    animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), 14.0f
                        )
                    )
                }
            } ?: showLocationError()
        })
    }

    private fun showLocationError() {
        showMessage(R.string.location_error)
    }

    override fun onPermissionDenied() {
        showMessage(R.string.enable_location)
    }

    override fun onPermissionPermanentlyDenied() {
        showMessage(R.string.enable_location_settings)
    }

    private fun showMessage(@StringRes resId: Int) {
        Snackbar.make(binding.root, resId, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fab).show()
    }
}