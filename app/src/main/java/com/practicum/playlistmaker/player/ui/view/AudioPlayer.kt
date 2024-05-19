package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.DATA_FROM_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayer : AppCompatActivity() {

    private val playerViewModel by viewModel<AudioPlayerViewModel>()

    private val binding by lazy { ActivityAudioPlayerBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val track: Track
        if (playerViewModel.getPlayerScreenState().value is Track) {
            track = playerViewModel.getPlayerScreenState().value!!
        } else {
            track = Gson().fromJson(
                intent.getStringExtra(DATA_FROM_AUDIO_PLAYER_KEY),
                Track::class.java
            )
            val audioPreviewData = track.previewUrl
            playerViewModel.prepared(audioPreviewData)
            playerViewModel.setPlayerScreenState(track)
        }

        playerViewModel.getPlayerStateLiveData().observe(this) {
            playbackControl(it)
            if (it == PlayerState.PREPARED) {
                setTime(getString(R.string.duration_preview_track_default))
            }
        }

        playerViewModel.getCurrentPositionLiveData().observe(this) {
            setTime(it)
        }

        initScreenPlayer(track)

        binding.buttonBackPlayer.setOnClickListener {
            finish()
        }

        binding.btnAddPlaylist.setOnClickListener {

        }

        binding.btnPlayPause.setOnClickListener {
            playerViewModel.togglePlaybackState()
        }

        binding.btnAddFavorites.setOnClickListener {

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

    private fun setTime(time: String) {
        binding.playbackProgress.text = time
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

}