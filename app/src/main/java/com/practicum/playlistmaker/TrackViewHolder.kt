package com.practicum.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val artwork = itemView.findViewById<ImageView>(R.id.artwork)
    private val trackName = itemView.findViewById<TextView>(R.id.trackName)
    private val artistName = itemView.findViewById<TextView>(R.id.artistName)
    private val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
    private val imgArtCornersRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2f,
        itemView.resources.displayMetrics
    ).toInt()

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.placeholder).transform(RoundedCorners(imgArtCornersRadius))
            .into(artwork)
    }


}

