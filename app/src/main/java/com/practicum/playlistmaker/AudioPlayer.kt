package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DURATION_DEFAULT = "00:00"
        private const val DELAY = 300L
    }

    private val handlerMain = Handler(Looper.getMainLooper())
    private val binding by lazy { ActivityAudioPlayerBinding.inflate(layoutInflater) }
    private var mediaPlayer = MediaPlayer()
    private var audioPreviewData = ""
    private var playerState = STATE_DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val track =
            Gson().fromJson(intent.getStringExtra(DATA_FROM_AUDIO_PLAYER_KEY), Track::class.java)
        audioPreviewData = track.previewUrl
        preparePlayer()
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

    private fun updateTimeDuration(): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerState) {
                    STATE_PLAYING -> {
                        val timeDurationFormat = formatDateInMinAndSec(mediaPlayer.currentPosition)
                        binding.playbackProgress.text = timeDurationFormat
                        handlerMain.postDelayed(this, DELAY)
                    }

                    STATE_PAUSED -> handlerMain.removeCallbacks(this)
                    STATE_PREPARED -> {
                        handlerMain.removeCallbacks(this)
                        binding.playbackProgress.text = DURATION_DEFAULT
                    }
                }
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(audioPreviewData)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.btnPlayPause.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.btnPlayPause.setImageResource(R.drawable.btn_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.btnPlayPause.setImageResource(R.drawable.btn_pause)
        playerState = STATE_PLAYING
        handlerMain.post(updateTimeDuration())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.btnPlayPause.setImageResource(R.drawable.btn_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun formatDateInMinAndSec(time: Any) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handlerMain.removeCallbacks(updateTimeDuration())
    }
}