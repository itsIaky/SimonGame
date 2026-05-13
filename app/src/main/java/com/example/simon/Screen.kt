package com.example.simon

sealed class Screen(val route: String) {
    object Game: Screen("game_screen")
    object Score: Screen("screen_screen")
    // Detail screen route with a required path parameter: the clicked "game score" index
    object Details: Screen("details_screen/{scoreIndex}") {
        // Navigation argument key used declaration/retrieval the argument
        const val ARG_SCORE_INDEX = "scoreIndex"

        // Builds a concrete route (e.g. details_screen/0) from a score index
        fun createRoute(scoreIndex: Int): String {
            return "details_screen/$scoreIndex"
        }
    }
}
