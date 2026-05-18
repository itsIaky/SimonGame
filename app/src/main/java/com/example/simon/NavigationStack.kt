package com.example.simon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationStack(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()
    val scoreViewModel: ScoreViewModel = viewModel()

    LaunchedEffect(gameViewModel) {
        gameViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                GameNavigationEvent.NavigateToScore -> {
                    val returnedToExistingScore = navController.popBackStack(
                        route = Screen.Score.route,
                        inclusive = false
                    )
                    if (!returnedToExistingScore) {
                        navController.navigate(Screen.Score.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Score.route) {
        composable(route = Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                modifier = modifier,
                onNavigateToScore = {
                    gameViewModel.requestNavigateToScore()
                }
            )
        }
        composable(route = Screen.Score.route) {
            ScoreScreen(
                modifier = modifier,
                viewModel = scoreViewModel,
                onNavigateToGame = {
                    gameViewModel.reset()
                    navController.navigate(Screen.Game.route)
                },
                onNavigateToDetails = { scoreId ->
                    navController.navigate(Screen.Details.createRoute(scoreId))
                }
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(Screen.Details.ARG_SCORE_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val scoreId = backStackEntry.arguments?.getLong(Screen.Details.ARG_SCORE_ID) ?: -1L
            val selectedScore by scoreViewModel.observePlayedGameById(scoreId).collectAsState(initial = null)

            selectedScore?.let { score ->
                DetailScreen(
                    modifier = modifier,
                    score = score
                )
            } ?: Text("Game not found")
        }
    }
}
