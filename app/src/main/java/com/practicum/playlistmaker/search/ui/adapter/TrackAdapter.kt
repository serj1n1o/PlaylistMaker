package com.practicum.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track

open class TrackAdapter<T>(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {
    var tracks = mutableListOf<Track>()

    interface TrackClickListener {
        val onItemClickListener: ((Track) -> Unit)?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val viewTrackBinding =
            TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding = viewTrackBinding, clickListener = clickListener)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

    }
}