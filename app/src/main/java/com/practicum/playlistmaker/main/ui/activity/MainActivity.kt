package com.practicum.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.medialibrary.ui.view.LibraryActivity
import com.practicum.playlistmaker.search.ui.view.SearchActivity
import com.practicum.playlistmaker.settings.ui.view.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        binding.libraryButton.setOnClickListener {
            val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        binding.settingsButton.setOnClickListener {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}