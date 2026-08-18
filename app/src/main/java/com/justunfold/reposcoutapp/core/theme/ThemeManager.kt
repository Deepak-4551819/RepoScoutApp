package com.justunfold.reposcoutapp.core.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun toggleTheme() {
        _themeMode.value = _themeMode.value.next()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
