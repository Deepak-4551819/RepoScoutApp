package com.justunfold.reposcoutapp.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.justunfold.reposcoutapp.core.theme.ThemeMode
import com.justunfold.reposcoutapp.features.bookmarks.BookmarksScreen
import com.justunfold.reposcoutapp.features.detail.DetailScreen
import com.justunfold.reposcoutapp.features.explore.ExploreScreen
import com.justunfold.reposcoutapp.features.search.SearchScreen

@Composable
fun AppNavigation(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show Bottom Bar only for top-level tabs (Explore & Saved)
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }

    Scaffold(
        // Zero out outer scaffold insets so child screens manage their own status bars without duplication
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Explore,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            // 1. Explore Screen
            composable<Route.Explore> {
                ExploreScreen(
                    currentTheme = currentTheme,
                    onThemeSelected = onThemeSelected,
                    onNavigateToSearch = { navController.navigate(Route.Search) },
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Route.Detail(owner = owner, repo = repo))
                    }
                )
            }

            // 2. Search Screen (Opened full screen from Top Bar Search icon)
            composable<Route.Search> {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Route.Detail(owner = owner, repo = repo))
                    }
                )
            }

            // 3. Saved / Bookmarks Screen
            composable<Route.Bookmarks> {
                BookmarksScreen(
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Route.Detail(owner = owner, repo = repo))
                    },
                    onNavigateToExplore = {
                        navController.navigate(Route.Explore) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 4. Detail Screen
            composable<Route.Detail> { backStackEntry ->
                val detailRoute = backStackEntry.toRoute<Route.Detail>()
                DetailScreen(
                    owner = detailRoute.owner,
                    repo = detailRoute.repo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
