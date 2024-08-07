package com.practicum.playlistmaker.player.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistViewForBottomsheetBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlayerPlaylistAdapter(
    private val clickListener: PlaylistClickListener,
) : RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    val playlists = mutableListOf<Playlist>()

    interface PlaylistClickListener {
        val onItemClickListener: ((Playlist) -> Unit)?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        val viewPlaylistBinding = PlaylistViewForBottomsheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerPlaylistViewHolder(
            binding = viewPlaylistBinding,
            clickListener = clickListener
        )
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}