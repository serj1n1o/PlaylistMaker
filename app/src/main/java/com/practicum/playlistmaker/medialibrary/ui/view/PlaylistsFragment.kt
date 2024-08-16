package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistState
import com.practicum.playlistmaker.medialibrary.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : FragmentWithBinding<FragmentPlaylistsBinding>() {

    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    private val playlistAdapter by lazy {
        PlaylistAdapter(object : PlaylistAdapter.PlaylistClickListener {
            override val onClickPlaylist: ((Playlist) -> Unit)
                get() = { playlist ->
                    if (clickDebounce()) {
                        playlist.id?.let { navigateItemPlaylist(it) }
                    }
                }
        })
    }

    private fun navigateItemPlaylist(playlistId: Long) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistItemFragment,
            PlaylistItemFragment.createArgs(playlistId)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistViewModel.getPlaylists()

        binding.recyclerViewPlaylists.adapter = playlistAdapter
        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_creatorPlaylistFragment)
        }

        playlistViewModel.getPlaylistState().observe(viewLifecycleOwner) { statePlaylist ->
            when (statePlaylist) {
                is PlaylistState.Content -> showContent(statePlaylist.playlists)
                PlaylistState.Empty -> showEmpty()
            }
        }

    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            placeholderEmptyPlaylist.isVisible = false
            recyclerViewPlaylists.isVisible = true
        }
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        with(binding) {
            placeholderEmptyPlaylist.isVisible = true
            recyclerViewPlaylists.isVisible = false
        }

    }


    override fun onDestroyView() {
        binding.recyclerViewPlaylists.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}