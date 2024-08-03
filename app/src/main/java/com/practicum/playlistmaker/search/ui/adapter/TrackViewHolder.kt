package com.practicum.playlistmaker.search.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track


class TrackViewHolder(
    private val binding: TrackViewBinding,
    private val clickListener: TrackAdapter.TrackClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        with(binding) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            artistName.requestLayout()
            trackTime.text = model.trackTime
            Glide.with(itemView).load(model.artworkUrl100).fitCenter()
                .placeholder(R.drawable.placeholder).transform(
                    RoundedCorners(
                        itemView.resources.getDimensionPixelSize(R.dimen.radius_2dp)
                    )
                )
                .into(artwork)
        }
        itemView.setOnClickListener { clickListener.onItemClickListener?.invoke(model) }
    }


}

