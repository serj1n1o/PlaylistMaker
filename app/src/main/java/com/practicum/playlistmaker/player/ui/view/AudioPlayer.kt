package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerState
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val track: Track
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

        initScreenPlayer(track)

        binding.buttonBackPlayer.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPlayPause.setOnClickListener {
            playerViewModel.togglePlaybackState()
        }

        binding.btnAddPlaylist.setOnClickListener {
            playerViewModel.addToPlaylist(track)
        }

        binding.btnAddFavorites.setOnClickListener {
            playerViewModel.addToFavorites(track)
        }


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

    companion object {
        const val DATA_FROM_AUDIO_PLAYER_KEY = "TRACK DATA"
        fun createArgs(track: Track): Bundle =
            bundleOf(
                DATA_FROM_AUDIO_PLAYER_KEY to track
            )
    }

}