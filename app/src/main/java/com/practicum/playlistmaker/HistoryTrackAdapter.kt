package com.practicum.playlistmaker

class HistoryTrackAdapter(private val tracks: List<Track>) : TrackAdapter<TrackViewHolder>(tracks) {
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
}