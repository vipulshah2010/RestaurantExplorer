package com.dott.restaurantexplorer.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dott.restaurantexplorer.databinding.FragmentRestaurantDetailsDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RestaurantDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRestaurantDetailsDialogBinding? = null
    private val binding get() = _binding!!

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
    }
}