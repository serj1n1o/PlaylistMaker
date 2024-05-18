package com.practicum.playlistmaker.search.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.models.Track

class HistoryTracksStorageImpl(context: Context) : HistoryTracksStorage {

    private val sharedPreferencesSearchHistory =
        context.getSharedPreferences(PREFERENCES_HISTORY, Context.MODE_PRIVATE)

    override fun addTrackToHistory(track: Track) {
        val listHistory = getTracksHistory()

        if (listHistory.any { track.trackId == it.trackId }) {
            listHistory.removeIf { track.trackId == it.trackId }
        }
        listHistory.add(0, track)
        if (listHistory.size > MAX_CAPACITY_LIST) {
            listHistory.removeLast()
        }

        sharedPreferencesSearchHistory.edit().putString(
            HISTORY_TRACK_KEY,
            Gson().toJson(listHistory)
        ).apply()
    }

    override fun getTracksHistory(): MutableList<Track> {
        val listHistoryTrackJson = sharedPreferencesSearchHistory.getString(HISTORY_TRACK_KEY, null)
        val listHistory = mutableListOf<Track>()
        if (listHistoryTrackJson != null) {
            listHistory.addAll(
                Gson().fromJson(
                    listHistoryTrackJson, object : TypeToken<List<Track>>() {}.type
                )
            )
        }
        return listHistory
    }

    override fun clearHistory() {
        sharedPreferencesSearchHistory.edit().remove(HISTORY_TRACK_KEY).apply()
    }

    private companion object {
        const val PREFERENCES_HISTORY = "PREFERENCES HISTORY"
        const val HISTORY_TRACK_KEY = "HISTORY TRACK KEY"
        const val MAX_CAPACITY_LIST = 10
    }
}