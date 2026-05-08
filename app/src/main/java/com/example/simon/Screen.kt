package com.example.simon

sealed class Screen(val route: String) {
    object Game: Screen("game_screen")
    object Score: Screen("screen_screen")
    object Details: Screen("details_screen")
}