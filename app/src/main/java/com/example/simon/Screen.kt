package com.example.simon

sealed class Screen(val route: String) {
    object Game: Screen("game_screen")
    object Score: Screen("screen_screen")
    object Details: Screen("details_screen/{scoreId}") {
        // Navigation argument key used declaration/retrieval the argument
        const val ARG_SCORE_ID = "scoreId"

        fun createRoute(scoreId: Long): String {
            return "details_screen/$scoreId"
        }
    }
}
