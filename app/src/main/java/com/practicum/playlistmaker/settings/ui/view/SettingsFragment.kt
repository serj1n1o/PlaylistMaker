package com.practicum.playlistmaker.settings.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : FragmentWithBinding<FragmentSettingsBinding>() {

    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchTheme.apply {
            settingsViewModel.getThemeStateLiveData().observe(viewLifecycleOwner) {
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