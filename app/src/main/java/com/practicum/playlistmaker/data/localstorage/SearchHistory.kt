package com.practicum.playlistmaker.data.localstorage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.HISTORY_TRACK_KEY
import com.practicum.playlistmaker.domain.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val MAX_CAPACITY_LIST = 10
        val historyList = mutableListOf<Track>()
    }

    fun loadHistoryList() {
        historyList.clear()
        val historyListJson = sharedPreferences.getString(HISTORY_TRACK_KEY, null)
        if (historyListJson != null) {
            historyList.addAll(
                Gson().fromJson(
                    historyListJson, object : TypeToken<List<Track>>() {}.type
                )
            )
        }

    }

    fun saveTrackToHistory(track: Track) {
        if (historyList.any { track.trackId == it.trackId }) {
            historyList.removeIf { track.trackId == it.trackId }
        }
        historyList.add(0, track)
        if (historyList.size > MAX_CAPACITY_LIST) {
            historyList.removeLast()
        }

        sharedPreferences.edit().putString(HISTORY_TRACK_KEY, Gson().toJson(historyList)).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_TRACK_KEY).apply()
        historyList.clear()

    }

}