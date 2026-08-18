package com.justunfold.reposcoutapp.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Formatters {
    fun formatMetricCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000.0)
            else -> count.toString()
        }
    }

    fun formatRelativeTime(isoDateString: String): String {
        if (isoDateString.isBlank()) return ""
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(isoDateString) ?: return ""
            val diffMillis = System.currentTimeMillis() - date.time
            val diffSeconds = diffMillis / 1000
            val diffMinutes = diffSeconds / 60
            val diffHours = diffMinutes / 60
            val diffDays = diffHours / 24

            when {
                diffDays > 30 -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
                diffDays > 0 -> "Updated ${diffDays}d ago"
                diffHours > 0 -> "Updated ${diffHours}h ago"
                diffMinutes > 0 -> "Updated ${diffMinutes}m ago"
                else -> "Updated just now"
            }
        } catch (e: Exception) {
            ""
        }
    }
}
