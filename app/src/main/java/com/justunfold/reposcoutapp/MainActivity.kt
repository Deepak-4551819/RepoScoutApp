package com.justunfold.reposcoutapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.justunfold.reposcoutapp.core.theme.ThemeManager
import com.justunfold.reposcoutapp.core.theme.ThemeMode
import com.justunfold.reposcoutapp.navigation.AppNavigation
import com.justunfold.reposcoutapp.ui.theme.RepoScoutAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            val currentTheme by themeManager.themeMode.collectAsStateWithLifecycle()
            val isDark = when (currentTheme) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            RepoScoutAppTheme(darkTheme = isDark) {
                AppNavigation(
                    currentTheme = currentTheme,
                    onThemeSelected = { newMode -> themeManager.setThemeMode(newMode) }
                )
            }
        }
    }
}
