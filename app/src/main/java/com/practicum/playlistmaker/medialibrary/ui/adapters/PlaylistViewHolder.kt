package com.practicum.playlistmaker.medialibrary.ui.adapters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.util.DataMapper

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding,
    private val clickListener: PlaylistAdapter.PlaylistClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        with(binding) {
            if (item.cover != null) {
                coverPlaylist.setImageURI(item.cover)
                coverPlaylist.scaleType = ImageView.ScaleType.CENTER_CROP

            } else {
                coverPlaylist.apply {
                    setImageResource(R.drawable.placeholder)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            }

            namePlaylist.text = item.name
            amountTracks.text = DataMapper.mapAmountTrackToString(item.amountTracks)
        }
        itemView.setOnClickListener { clickListener.onClickPlaylist?.invoke(item) }
    }
}