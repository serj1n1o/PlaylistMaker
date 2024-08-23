package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.EditPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatorPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    private lateinit var playlist: Playlist


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getLong(PLAYLIST_EDIT_FRAGMENT_KEY)

        if (viewModel.getPlaylistData().value !is Playlist) {
            viewModel.loadPlaylist(playlistId)
        }


        viewModel.getPlaylistData().observe(viewLifecycleOwner) { playlist ->
            initScreen(playlist)
            this@EditPlaylistFragment.playlist = playlist
        }

        binding.coverPlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        binding.btnCreatePlaylist.setOnClickListener {
            lifecycleScope.launch {
                val cover = coverUri?.let { uri ->
                    viewModel.saveImageToStorage(
                        coverUri = uri,
                        namePlaylist = binding.editName.text.toString()
                    )
                } ?: playlist.cover
                val newPlaylist = playlist.copy(
                    name = binding.editName.text.toString(),
                    description = binding.editDescription.text?.toString(),
                    cover = cover
                )
                viewModel.updatePlaylist(newPlaylist)
                findNavController().popBackStack()
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

    }

    private fun initScreen(playlist: Playlist?) {
        playlist?.let {
            with(binding) {
                editName.setText(it.name)
                editDescription.setText(it.description)
                btnCreatePlaylist.text = getString(R.string.save_txt)

                it.cover?.let { uri ->
                    coverPlaylist.setImageURI(uri)
                    coverPlaylist.scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }


    companion object {
        private const val PLAYLIST_EDIT_FRAGMENT_KEY = "edit fragment"
        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_EDIT_FRAGMENT_KEY to playlistId)
    }
}