package com.practicum.playlistmaker.settings.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonBackSettings.setOnClickListener {
            finish()
        }


        binding.switchTheme.apply {
            settingsViewModel.getThemeStateLiveData().observe(this@SettingsActivity) {
                isChecked = it.isDarkTheme
            }

            setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.switchTheme(isChecked)
            }
        }



        binding.buttonShareApp.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.buttonSendSupport.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.buttonUserAgreement.setOnClickListener {
            settingsViewModel.openTerms()
        }

    }
}