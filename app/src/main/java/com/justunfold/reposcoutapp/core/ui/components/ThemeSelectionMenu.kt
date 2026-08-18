package com.justunfold.reposcoutapp.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.justunfold.reposcoutapp.core.theme.ThemeMode

@Composable
fun ThemeSelectionMenu(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = when (currentTheme) {
                    ThemeMode.SYSTEM -> Icons.Outlined.Brightness4
                    ThemeMode.LIGHT -> Icons.Outlined.LightMode
                    ThemeMode.DARK -> Icons.Outlined.DarkMode
                },
                contentDescription = "Select Theme"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemeOptionItem(
                title = "System Default",
                icon = Icons.Outlined.SettingsBrightness,
                isSelected = currentTheme == ThemeMode.SYSTEM,
                onClick = {
                    onThemeSelected(ThemeMode.SYSTEM)
                    expanded = false
                }
            )

            ThemeOptionItem(
                title = "Light Mode",
                icon = Icons.Outlined.LightMode,
                isSelected = currentTheme == ThemeMode.LIGHT,
                onClick = {
                    onThemeSelected(ThemeMode.LIGHT)
                    expanded = false
                }
            )

            ThemeOptionItem(
                title = "Dark Mode",
                icon = Icons.Outlined.DarkMode,
                isSelected = currentTheme == ThemeMode.DARK,
                onClick = {
                    onThemeSelected(ThemeMode.DARK)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun ThemeOptionItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        trailingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = onClick
    )
}
