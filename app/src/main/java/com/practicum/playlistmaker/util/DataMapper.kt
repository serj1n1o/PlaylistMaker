package com.practicum.playlistmaker.util

import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import java.util.Date
import java.util.Locale

object DataMapper {
    fun mapTimeMillisToMinAndSec(time: Any): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    fun mapDateToYear(date: Date?): String {
        return if (date != null) DateFormat.format("yyyy", date).toString() else ""
    }

    fun mapAmountTrackToString(amount: Int): String {
        val string = when {
            amount % 10 == 1 -> "трек"
            amount % 10 in 2..4 -> "трека"
            amount % 100 in 11..19 -> "треков"
            else -> "треков"
        }
        return "$amount $string"
    }
}