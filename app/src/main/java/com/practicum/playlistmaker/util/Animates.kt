package com.practicum.playlistmaker.util

import android.view.View
import androidx.core.view.isVisible


object Animates {

    fun animateOverlay(show: Boolean, view: View, duration: Long = 300L) {
        val alpha = if (show) 1f else 0f
        view.animate()
            .alpha(alpha)
            .setDuration(duration)
            .withEndAction { if (!show) view.isVisible = false }
    }

}