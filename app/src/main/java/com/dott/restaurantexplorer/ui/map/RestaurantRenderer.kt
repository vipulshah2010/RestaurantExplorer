package com.dott.restaurantexplorer.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.dott.restaurantexplorer.R
import com.dott.restaurantexplorer.api.model.Venue
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 *  Changes marker icon and colors clusters dynamically depending on cluster size. It looks more appealing to user
 *  to view colors, and found populated restaurant areas.
 */
class RestaurantRenderer(
    private val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<Venue>?
) : DefaultClusterRenderer<Venue>(context, map, clusterManager) {

    /**
     * Customized marker icon
     */
    override fun onBeforeClusterItemRendered(item: Venue, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        ContextCompat.getDrawable(context, R.drawable.ic_marker)?.let {
            val drawableMarker = BitmapDescriptorFactory
                .fromBitmap(drawableToBitmap(it))
            markerOptions.icon(drawableMarker)
        }
    }

    /**
     * {@return}
     * Green -> Cluster size between 0 & 5
     * Yellow -> Cluster size between 6 & 10
     * Orange -> Cluster size between 11 & 20
     * Red -> Cluster size more than 20
     */
    override fun getColor(clusterSize: Int): Int = when (clusterSize) {
        in 0..5 -> ContextCompat.getColor(context, R.color.map_green)
        in 6..10 -> ContextCompat.getColor(context, R.color.map_yellow)
        in 11..20 -> ContextCompat.getColor(context, R.color.map_orange)
        else -> ContextCompat.getColor(context, R.color.map_red)
    }
}

/**
 * Converting svg to bitmap
 */
private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    val bitmap =
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}