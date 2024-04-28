package com.practicum.playlistmaker.ui.adapters

import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.domain.models.Track
import java.util.Locale


class TrackViewHolder(private val binding: TrackViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val imgArtCornersRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2f,
        itemView.resources.displayMetrics
    ).toInt()

    fun bind(model: Track) {
        with(binding) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            artistName.requestLayout()
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
            Glide.with(itemView).load(model.artworkUrl100).fitCenter()
                .placeholder(R.drawable.placeholder).transform(RoundedCorners(imgArtCornersRadius))
                .into(artwork)
        }
    }


}

