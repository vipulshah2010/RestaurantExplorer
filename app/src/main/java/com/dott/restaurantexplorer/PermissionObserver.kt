package com.dott.restaurantexplorer

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Lifecycle aware permission observer, it will auto de-registered on Activity#onDestroy.
 */
class PermissionObserver(
    private val activity: FragmentActivity,
    private val permission: String,
    private val callback: PermissionCallback
) : DefaultLifecycleObserver {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        requestPermissionLauncher =
            activity.activityResultRegistry.register(permission,
                owner,
                ActivityResultContracts.RequestPermission(), { isGranted: Boolean ->
                    if (isGranted) {
                        callback.onPermissionGranted()
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                activity,
                                permission
                            )
                        ) {
                            callback.onPermissionPermanentlyDenied()
                        } else {
                            callback.onPermissionDenied()
                        }
                    }
                })
    }

    fun requestPermission() {
        requestPermissionLauncher.launch(permission)
    }
}

interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onPermissionPermanentlyDenied()
}
