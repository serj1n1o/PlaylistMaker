package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val track =
            Gson().fromJson(intent.getStringExtra(DATA_FROM_AUDIO_PLAYER_KEY), Track::class.java)

        fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val imgArtCornersRadiusInAudioPlayer = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f, this.resources.displayMetrics
        ).toInt()

        binding.apply {
            trackNamePlayer.text = track.trackName
            artistNamePlayer.text = track.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            playbackProgress.text = trackTime.text
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

        }

        binding.btnAddFavorites.setOnClickListener {

        }
    }
}