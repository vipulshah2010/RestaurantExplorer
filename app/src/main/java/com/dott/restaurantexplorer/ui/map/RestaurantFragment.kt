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
import androidx.navigation.fragment.findNavController
import com.dott.restaurantexplorer.PermissionCallback
import com.dott.restaurantexplorer.PermissionObserver
import com.dott.restaurantexplorer.R
import com.dott.restaurantexplorer.api.model.Venue
import com.dott.restaurantexplorer.api.model.VenueResult
import com.dott.restaurantexplorer.databinding.FragmentRestaurantBinding
import com.dott.restaurantexplorer.ui.RestaurantViewModel
import com.dott.restaurantexplorer.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
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
class RestaurantFragment : BaseFragment<FragmentRestaurantBinding>(), PermissionCallback,
    OnClusterClickListener<Venue>, ClusterManager.OnClusterItemClickListener<Venue> {

    private lateinit var permissionObserver: PermissionObserver
    private var googleMap: GoogleMap? = null
    private lateinit var map: SupportMapFragment

    private val viewModel: RestaurantViewModel by activityViewModels()

    private lateinit var clusterManager: ClusterManager<Venue>

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
                is VenueResult.Error -> {
                    showMessage(it.message ?: requireContext().getString(it.code))
                }
                is VenueResult.Success -> {
                    if (::clusterManager.isInitialized) {
                        clusterManager.addItems(it.data)
                        clusterManager.cluster()
                    }
                }
            }
        })

        viewModel.selectedVenueLiveData.observe(requireActivity(), {
            /**
             * If fragment state is restored, LiveData value will be re-emitted,
             * re-performing navigation would crash app with illegalState, so first dismiss any outstanding bottomSheet Fragment
             */
            if (findNavController().currentDestination?.id == R.id.restaurantDetailsDialogFragment) {
                findNavController().popBackStack()
            }
            findNavController().navigate(
                RestaurantFragmentDirections.actionVenueToVenueDetails(it)
            )
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            googleMap = map.awaitMap()

            googleMap?.let {
                binding.fab.isVisible = true
                setupClusterManager(it)
                permissionObserver.requestPermission()

                it.cameraEvents().collect { event ->

                    when (event) {
                        is CameraIdleEvent -> {
                            it.cameraPosition?.target?.let { target ->
                                it.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                                    viewModel.getVenues(
                                        target,
                                        viewModel.getMapVisibleRadius(it.projection),
                                        bounds
                                    )
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

    /*
    * A function that sets up the cluster manager to be used with the map
    * */
    private fun setupClusterManager(googleMap: GoogleMap) {
        //Initializing the cluster manager
        clusterManager = ClusterManager(requireActivity(), googleMap)
        clusterManager.setAnimation(true)

        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemClickListener(this)

        //Setting the custom renderer to change the marker and cluster views
        clusterManager.renderer = RestaurantRenderer(requireContext(), googleMap, clusterManager)
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

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok, {})
            .setAnchorView(binding.fab).show()
    }

    private fun showMessage(@StringRes resId: Int) {
        showMessage(requireContext().getString(resId))
    }

    override fun onClusterClick(cluster: Cluster<Venue>?): Boolean {
        cluster?.let {
            val builder = LatLngBounds.builder()
            for (item in it.items) {
                builder.include(item.position)
            }
            val bounds = builder.build()

            googleMap?.run {
                animateCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
                return true
            }
        }

        return false
    }

    override fun onClusterItemClick(item: Venue?): Boolean {
        item?.let {
            viewModel.onSelectVenue(item)
            return true
        }
        return false
    }
}