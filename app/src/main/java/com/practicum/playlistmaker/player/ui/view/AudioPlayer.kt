package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistState
import com.practicum.playlistmaker.medialibrary.domain.model.StatusAdd
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.adapters.PlayerPlaylistAdapter
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayer : FragmentWithBinding<FragmentAudioPlayerBinding>() {

    private val playerViewModel by viewModel<AudioPlayerViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAudioPlayerBinding {
        return FragmentAudioPlayerBinding.inflate(inflater, container, false)
    }

    lateinit var track: Track

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val playlistAdapter by lazy {
        PlayerPlaylistAdapter(
            object : PlayerPlaylistAdapter.PlaylistClickListener {
                override val onItemClickListener: ((Playlist) -> Unit)
                    get() = { playlist ->
                        playerViewModel.addToPlaylist(track, playlist)
                        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                    }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlayer)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN


        if (playerViewModel.getPlayerScreenState().value is Track) {
            track = playerViewModel.getPlayerScreenState().value!!
        } else {
            track = BundleCompat.getParcelable(
                requireArguments(),
                DATA_FROM_AUDIO_PLAYER_KEY,
                Track::class.java
            ) as Track

            val audioPreviewData = track.previewUrl
            playerViewModel.prepared(audioPreviewData)
            playerViewModel.setPlayerScreenState(track)
        }

        binding.recyclerViewPlaylistsFromPlayer.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewPlaylistsFromPlayer.adapter = playlistAdapter

        playerViewModel.getPlayerStateLiveData().observe(viewLifecycleOwner) {
            playbackControl(it)
            if (it == PlayerState.PREPARED) {
                setTime(getString(R.string.duration_preview_track_default))
            }
        }

        playerViewModel.getCurrentPositionLiveData().observe(viewLifecycleOwner) {
            setTime(it)
        }

        playerViewModel.getPlayerScreenState().observe(viewLifecycleOwner) {
            setFavoritesBtnState(it.inFavorite)
        }

        playerViewModel.getPlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    showContent(state.playlists)
                }

                PlaylistState.Empty -> {
                    hidePlaylists()
                }
            }
        }

        playerViewModel.getTrackStatusAdd().observe(viewLifecycleOwner) { status ->
            when (status) {
                is StatusAdd.Success -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_add_playlist, status.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is StatusAdd.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_do_not_add_playlist, status.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        initScreenPlayer(track)

        binding.buttonBackPlayer.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPlayPause.setOnClickListener {
            playerViewModel.togglePlaybackState()
        }

        binding.btnAddPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            playerViewModel.getPlaylists()

        }

        binding.btnAddFavorites.setOnClickListener {
            playerViewModel.addToFavorites(track)
        }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.isVisible = true
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        binding.newPlaylistBtnFromPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayer_to_itemPlaylistFragment)
        }


    }


    private fun hidePlaylists() {
        binding.recyclerViewPlaylistsFromPlayer.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerViewPlaylistsFromPlayer.isVisible = true
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun playbackControl(state: PlayerState) {
        when (state) {
            PlayerState.PAUSED, PlayerState.PREPARED -> {
                binding.btnPlayPause.setImageResource(R.drawable.btn_play)
            }

            PlayerState.PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.btn_pause)
            }

            else -> Unit
        }

    }

    private fun initScreenPlayer(track: Track) {
        fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val imgArtCornersRadiusInAudioPlayer = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f, this.resources.displayMetrics
        ).toInt()

        with(binding) {
            trackNamePlayer.text = track.trackName
            artistNamePlayer.text = track.artistName
            trackTime.text = track.trackTime
            trackCollection.text = track.collectionName
            trackReleaseDate.text = track.releaseYear
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
            Glide.with(this@AudioPlayer).load(getCoverArtwork()).fitCenter()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(imgArtCornersRadiusInAudioPlayer))
                .into(binding.artworkPlayer)
        }
    }

    private fun setFavoritesBtnState(inFavorites: Boolean) {
        if (inFavorites) binding.btnAddFavorites.setImageResource(R.drawable.btn_added_to_favorite)
        else binding.btnAddFavorites.setImageResource(R.drawable.btn_add_favorites)
    }

    private fun setTime(time: String) {
        binding.playbackProgress.text = time
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }


    override fun onDestroyView() {
        playlistAdapter.playlists.clear()
        binding.recyclerViewPlaylistsFromPlayer.adapter = null
        bottomSheetBehavior = null
        super.onDestroyView()

    }

    companion object {
        const val DATA_FROM_AUDIO_PLAYER_KEY = "TRACK DATA"
        fun createArgs(track: Track): Bundle =
            bundleOf(
                DATA_FROM_AUDIO_PLAYER_KEY to track
            )
    }

}