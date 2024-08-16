package com.practicum.playlistmaker.medialibrary.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {
    val playlists = mutableListOf<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val viewPlaylistBinding =
            PlaylistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(viewPlaylistBinding)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) =
        holder.bind(playlists[position])

}