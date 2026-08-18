package com.justunfold.reposcoutapp.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Explore : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Bookmarks : Route

    @Serializable
    data class Detail(val owner: String, val repo: String) : Route
}
