package com.justunfold.reposcoutapp.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object CustomTabsHelper {
    fun openCustomTab(context: Context, url: String) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback to standard browser intent if Custom Tabs is not available
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    fun shareRepository(context: Context, name: String, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out $name on GitHub")
            putExtra(Intent.EXTRA_TEXT, "$name - $url")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share repository via"))
    }
}
