package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding
import com.practicum.playlistmaker.medialibrary.ui.adapters.LibraryAdapter

class LibraryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLibraryBinding.inflate(layoutInflater) }

    private lateinit var tabLibraryMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.pagerLibrary.adapter =
            LibraryAdapter(fragmentManager = supportFragmentManager, lifecycle = lifecycle)

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

        tabLibraryMediator.attach()

        binding.buttonBackLibrary.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tabLibraryMediator.detach()
    }

}