package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistState
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : FragmentWithBinding<FragmentPlaylistBinding>() {

    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.getPlaylistState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistState.Content -> TODO()
                PlaylistState.Empty -> binding.placeholderEmptyPlaylist.isVisible = true
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}