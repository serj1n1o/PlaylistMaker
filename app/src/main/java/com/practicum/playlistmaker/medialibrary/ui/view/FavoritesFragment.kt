package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesState
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : FragmentWithBinding<FragmentFavoritesBinding>() {

    private val favoritesViewModel by viewModel<FavoritesViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel.getFavoritesState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoritesState.Content -> TODO()
                FavoritesState.Empty -> binding.placeholderEmptyFavorites.isVisible = true
            }
        }
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

}