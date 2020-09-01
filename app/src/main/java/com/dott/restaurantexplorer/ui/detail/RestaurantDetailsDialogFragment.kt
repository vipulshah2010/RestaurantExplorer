package com.dott.restaurantexplorer.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import coil.api.load
import coil.transform.CircleCropTransformation
import com.dott.restaurantexplorer.R
import com.dott.restaurantexplorer.databinding.FragmentRestaurantDetailsDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Simple Dialog displayed basic details of selected restaurant
 */
class RestaurantDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRestaurantDetailsDialogBinding? = null
    private val binding get() = _binding!!

    private val args: RestaurantDetailsDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modalBottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.state = STATE_EXPANDED

        with(args.venue) {
            binding.titleText.text = title
            binding.subtitleText.text = location.formattedAddress.joinToString("\n")

            with(categories[0]) {
                binding.imageView.load(icon.prefix + "100" + icon.suffix) {
                    crossfade(true)
                    placeholder(R.drawable.ic_svg_placeholder)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}