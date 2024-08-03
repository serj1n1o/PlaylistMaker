package com.practicum.playlistmaker.player.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewForBottomsheetBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlayerPlaylistViewHolder(
    private val binding: PlaylistViewForBottomsheetBinding,
    private val clickListener: PlayerPlaylistAdapter.PlaylistClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        with(binding) {
            Glide.with(itemView)
                .load(item.cover)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.radius_2dp)))
                .fitCenter()
                .into(coverPlaylist)
            playlistName.text = item.name
            amountTracks.text = item.amountTracks.toString()
        }
        itemView.setOnClickListener {
            clickListener.onItemClickListener?.invoke(item)
        }
    }
}