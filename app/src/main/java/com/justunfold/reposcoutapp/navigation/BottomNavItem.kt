package com.justunfold.reposcoutapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: Route,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Explore : BottomNavItem(
        route = Route.Explore,
        title = "Explore",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    )

    data object Bookmarks : BottomNavItem(
        route = Route.Bookmarks,
        title = "Saved",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    )
}

// Only Explore and Saved in the Bottom Bar
val bottomNavItems = listOf(
    BottomNavItem.Explore,
    BottomNavItem.Bookmarks
)
