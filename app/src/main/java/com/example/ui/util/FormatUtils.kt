package com.example.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {

    private val trLocale = Locale("tr", "TR")

    fun formatPointsCompact(points: Long): String {
        return when {
            points >= 1_000_000_000L -> String.format(trLocale, "%.1fMr", points / 1_000_000_000.0)
            points >= 1_000_000L -> String.format(trLocale, "%.1fM", points / 1_000_000.0)
            points >= 100_000L -> String.format(trLocale, "%.0fB", points / 1_000.0)
            else -> NumberFormat.getIntegerInstance(trLocale).format(points)
        }
    }

    fun formatPointsFull(points: Long): String {
        return NumberFormat.getIntegerInstance(trLocale).format(points)
    }

    fun formatMultiplier(multiplier: Long): String {
        return when {
            multiplier >= 1_000_000L -> "1.000.000x"
            else -> "${NumberFormat.getIntegerInstance(trLocale).format(multiplier)}x"
        }
    }

    fun formatCountdown(remainingMillis: Long): String {
        val totalSeconds = (remainingMillis / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun formatShortDate(epochMillis: Long): String {
        val fmt = SimpleDateFormat("dd MMM HH:mm", trLocale)
        return fmt.format(Date(epochMillis))
    }
}
