package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryBinding
import com.practicum.playlistmaker.medialibrary.ui.adapters.LibraryAdapter
import com.practicum.playlistmaker.util.FragmentWithBinding

class LibraryFragment : FragmentWithBinding<FragmentLibraryBinding>() {

    private var tabLibraryMediator: TabLayoutMediator? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentLibraryBinding {
        return FragmentLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.pagerLibrary.adapter =
            LibraryAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle
            )

        tabLibraryMediator =
            TabLayoutMediator(binding.tabLibrary, binding.pagerLibrary) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.favorites_tracks_txt)
                    }

                    1 -> {
                        tab.text = getString(R.string.playlists_txt)
                    }
                }
            }

        tabLibraryMediator?.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLibraryMediator?.detach()
        tabLibraryMediator = null
    }

}