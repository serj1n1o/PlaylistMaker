package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesState
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.view.AudioPlayer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : FragmentWithBinding<FragmentFavoritesBinding>() {

    private val favoritesViewModel by viewModel<FavoritesViewModel>()

    private val trackAdapter by lazy {
        TrackAdapter<Any>(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: ((Track) -> Unit)
                    get() = {
                        if (clickDebounce()) startAudioPlayer(it)
                    }
                override val onLongTrackClickListener: ((Track) -> Boolean)?
                    get() = { false }
            }
        )
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesViewModel.getFavoritesTrack()
        binding.recyclerFavoritesTracks.adapter = trackAdapter

        favoritesViewModel.getFavoritesState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Content -> showContent(tracks = state.favoritesList)
                FavoritesState.Empty -> showEmpty()
            }
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            placeholderEmptyFavorites.isVisible = false
            recyclerFavoritesTracks.isVisible = true
        }
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        with(binding) {
            placeholderEmptyFavorites.isVisible = true
            recyclerFavoritesTracks.isVisible = false
        }
    }

    private fun startAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_audioPlayer,
            AudioPlayer.createArgs(track)
        )
    }

    override fun onDestroyView() {
        binding.recyclerFavoritesTracks.adapter = null
        trackAdapter.tracks.clear()
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

}