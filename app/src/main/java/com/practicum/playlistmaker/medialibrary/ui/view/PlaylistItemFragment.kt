package com.practicum.playlistmaker.medialibrary.ui.view

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistItemBinding
import com.practicum.playlistmaker.medialibrary.domain.model.ItemPlaylistState
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.TrackPlaylistState
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.ItemPlaylistViewModel
import com.practicum.playlistmaker.player.ui.view.AudioPlayer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import com.practicum.playlistmaker.util.Animates
import com.practicum.playlistmaker.util.DataMapper
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistItemFragment : FragmentWithBinding<FragmentPlaylistItemBinding>() {

    private val viewModel by viewModel<ItemPlaylistViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistItemBinding {
        return FragmentPlaylistItemBinding.inflate(inflater, container, false)
    }

    private var trackId: Long? = null

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private val confirmDialogDeleteTrack by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogThemeDeletePlaylist)
            .setTitle(getString(R.string.allert_dialog_txt_delete_track))
            .setMessage(getString(R.string.allert_dialog_message_delete_track))
            .setNeutralButton(getString(R.string.cancel_txt)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.delete_txt)) { dialog, which ->
                trackId?.let { viewModel.removeTrackFromPlaylist(it) }
            }
    }

    private val confirmDialogDeletePlaylist by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogThemeDeletePlaylist)
            .setTitle(getString(R.string.allert_dialog_title_delete_playlist))
            .setMessage(getString(R.string.allert_dialog_message_delete_playlist))
            .setNeutralButton(getString(R.string.cancel_txt)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.delete_txt)) { dialog, which ->
                viewModel.removePlaylist()
                findNavController().popBackStack()
            }
    }

    private val trackAdapter by lazy {
        TrackAdapter<Any>(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: (Track) -> Unit
                    get() = {
                        if (clickDebounce()) startAudioPlayer(it)
                    }
                override val onLongTrackClickListener: (Track) -> Boolean
                    get() = {
                        trackId = it.trackId
                        confirmDialogDeleteTrack.show()
                        true
                    }
            }
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = binding.bottomSheetMenuPlaylist?.let { BottomSheetBehavior.from(it) }
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN


        val playlistId = requireArguments().getLong(PLAYLIST_KEY)

        viewModel.getPlaylist(playlistId)

        binding.recyclerViewTracks.apply {
            adapter = trackAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        viewModel.getPlaylistState().observe(viewLifecycleOwner) { state ->
            renderPlaylist(state)

        }

        viewModel.getTrackState.observe(viewLifecycleOwner) { state ->
            renderTracks(state)
        }


        viewModel.getTotalDurationTracksAndAmount().observe(viewLifecycleOwner) { data ->
            updateTimeAndAmountTracks(data.duration, data.amount)
        }


        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.menuPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay?.isVisible = true
                        binding.overlay?.let { Animates.animateOverlay(true, it) }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay?.isVisible = false
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.share?.setOnClickListener {
            sharePlaylist()
        }

        binding.editInfo?.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistItemFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlistId)
            )
        }

        binding.deletePlaylist?.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDialogDeletePlaylist.show()
        }


    }

    private fun sharePlaylist() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        if (viewModel.getTrackState.value == TrackPlaylistState.Empty) {
            Toast.makeText(
                requireContext(),
                getString(R.string.share_empty_playlist),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sharePlaylist()
        }
    }


    private fun renderPlaylist(state: ItemPlaylistState) {
        when (state) {
            is ItemPlaylistState.Content -> {
                initScreenPlayer(state.playlist)
                initPlaylistForMenu(state.playlist)
            }

            ItemPlaylistState.Empty -> Unit
        }
    }


    private fun renderTracks(state: TrackPlaylistState) {
        when (state) {
            is TrackPlaylistState.Content -> showContentTracks(state.tracks)
            TrackPlaylistState.Empty -> hideContentTracks()
        }
    }

    private fun showContentTracks(tracks: List<Track>) {
        binding.recyclerViewTracks.isVisible = true
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun hideContentTracks() {
        binding.recyclerViewTracks.isVisible = false
    }

    private fun updateTimeAndAmountTracks(time: String, amount: Int) {
        with(binding) {
            durationAllTracks.text = time
            amountTracks.text = DataMapper.mapAmountTrackToString(amount)
        }
    }

    private fun initPlaylistForMenu(playlist: Playlist) {
        val imgArtCornersRadiusInAudioPlayer = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2f, this.resources.displayMetrics
        ).toInt()
        binding.coverPlaylistMenu?.let {
            Glide.with(requireContext()).load(playlist.cover).fitCenter()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(imgArtCornersRadiusInAudioPlayer))
                .into(it)
        }
        with(binding) {
            namePlaylistMenu?.text = playlist.name
            amountTracksMenu?.text = DataMapper.mapAmountTrackToString(playlist.amountTracks)

        }

    }

    private fun initScreenPlayer(playlist: Playlist) {
        with(binding) {
            if (playlist.cover == null) {
                coverPlaylist.setImageResource(R.drawable.placeholder_312)
            } else {
                coverPlaylist.setImageURI(playlist.cover)
                coverPlaylist.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            namePlaylist.text = playlist.name
            if (playlist.description.isNullOrEmpty()) {
                descriptionPlaylist.isVisible = false
            } else {
                descriptionPlaylist.text = playlist.description
                descriptionPlaylist.isVisible = true
            }
        }
    }

    private fun startAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistItemFragment_to_audioPlayer,
            AudioPlayer.createArgs(track)
        )
    }

    override fun onDestroyView() {
        trackAdapter.tracks.clear()
        binding.recyclerViewTracks.adapter = null
        bottomSheetBehavior = null
        super.onDestroyView()
    }

    companion object {
        private const val PLAYLIST_KEY = "playlist key"
        fun createArgs(playlistId: Long): Bundle = bundleOf(PLAYLIST_KEY to playlistId)

    }
}