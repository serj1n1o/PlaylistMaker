package com.practicum.playlistmaker.ui.player

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.DATA_FROM_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    companion object {
        private const val DELAY = 300L
    }

    private val handlerMain = Handler(Looper.getMainLooper())
    private val binding by lazy { ActivityAudioPlayerBinding.inflate(layoutInflater) }
    private val mediaPlayer by lazy { Creator.providePlayerManager() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val track =
            Gson().fromJson(intent.getStringExtra(DATA_FROM_AUDIO_PLAYER_KEY), Track::class.java)
        val audioPreviewData = track.previewUrl

        mediaPlayer.preparePlayer(audioPreviewData)

        fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val imgArtCornersRadiusInAudioPlayer = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f, this.resources.displayMetrics
        ).toInt()

        with(binding) {
            trackNamePlayer.text = track.trackName
            artistNamePlayer.text = track.artistName
            trackTime.text = formatDateInMinAndSec(track.trackTimeMillis)
            trackCollection.text = track.collectionName
            trackReleaseDate.text = DateFormat.format("yyyy", track.releaseDate)
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
            Glide.with(this@AudioPlayer).load(getCoverArtwork()).fitCenter()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(imgArtCornersRadiusInAudioPlayer))
                .into(binding.artworkPlayer)
        }

        binding.buttonBackPlayer.setOnClickListener {
            finish()
        }

        binding.btnAddPlaylist.setOnClickListener {

        }

        binding.btnPlayPause.setOnClickListener {
            playbackControl()
        }

        binding.btnAddFavorites.setOnClickListener {

        }
    }

    private fun playbackControl() {
        when (mediaPlayer.getPlayerState()) {
            PlayerState.PAUSED, PlayerState.PREPARED -> {
                mediaPlayer.startPlayer()
                binding.btnPlayPause.setImageResource(R.drawable.btn_pause)
                handlerMain.post(updateTimeDuration())
            }

            PlayerState.PLAYING -> {
                mediaPlayer.pausePlayer()
                binding.btnPlayPause.setImageResource(R.drawable.btn_play)
            }

            else -> {
                Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateTimeDuration(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayer.getPlayerState()) {
                    PlayerState.PLAYING -> {
                        val timeDurationFormat =
                            formatDateInMinAndSec(mediaPlayer.currentPosition())
                        binding.playbackProgress.text = timeDurationFormat
                        handlerMain.postDelayed(this, DELAY)
                    }

                    PlayerState.PAUSED -> handlerMain.removeCallbacks(this)

                    PlayerState.PREPARED -> {
                        handlerMain.removeCallbacks(this)
                        binding.playbackProgress.text =
                            getString(R.string.duration_preview_track_default)
                        binding.btnPlayPause.setImageResource(R.drawable.btn_play)
                    }

                    else -> {
                        Toast.makeText(
                            this@AudioPlayer,
                            getString(R.string.some_error), Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun formatDateInMinAndSec(time: Any) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)


    override fun onPause() {
        super.onPause()
        mediaPlayer.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handlerMain.removeCallbacks(updateTimeDuration())
    }
}