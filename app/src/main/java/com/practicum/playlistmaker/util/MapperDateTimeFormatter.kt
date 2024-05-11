package com.practicum.playlistmaker.util

import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import java.util.Date
import java.util.Locale

object MapperDateTimeFormatter {
    fun mapTimeMillisToMinAndSec(time: Any): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    fun mapDateToYear(date: Date?): String {
        return if (date != null) DateFormat.format("yyyy", date).toString() else ""
    }
}