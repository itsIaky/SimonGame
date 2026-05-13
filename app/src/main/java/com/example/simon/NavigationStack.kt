package com.example.simon

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavigationStack(modifier: Modifier = Modifier) {
    // Create a NavController to manage navigation between screens
    // Create ViewModel instances for both GameScreen and ScoreScreen to safe state across navigation
    val navController = rememberNavController()
    val gameViewModel : GameViewModel = viewModel()
    val scoreViewModel : ScoreViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Score.route) {
        composable(route = Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                modifier = modifier,
                // onNavigateToScore is a lambda function that will be called when the user finishes a game and wants to navigate to the ScoreScreen
                onNavigateToScore = {
                    if(!gameViewModel.isGameActive && !gameViewModel.failed) {
                        navController.navigate(Screen.Score.route)
                        return@GameScreen
                    }

                    if (gameViewModel.isPresentingSequence && gameViewModel.getGameSequence().size == 1) {
                        navController.navigate(Screen.Score.route)
                        return@GameScreen

                    }

                    scoreViewModel.addPlayedGameSequence(
                        gameScore = Score(
                            playedGamesSequence = gameViewModel.getGameSequence(),
                            playedUserSequence = gameViewModel.getUserSequence(),
                            maxCorrectSequence = gameViewModel.getGameSequence().size -1,
                            errorPosition = gameViewModel.getCurrentStep()
                        )
                    )
                    gameViewModel.clearUserSequence()

                    navController.navigate(Screen.Score.route)
                }
            )
        }
        composable(
            route = Screen.Score.route,
        ) {
            // Score list screen. Each card click sends the selected index to the details route.
            ScoreScreen(
                modifier = modifier,
                viewModel = scoreViewModel,
                onNavigateToGame = {
                    gameViewModel.reset()
                    navController.navigate(Screen.Game.route)
                },
                onNavigateToDetails = { scoreIndex ->
                    navController.navigate(Screen.Details.createRoute(scoreIndex))
                }
            )
        }
        composable(
            // Details destination expects one Int nav argument, the "game score" index
            route = Screen.Details.route,
            // this screen expects an argument named scoreIndex, and it must be an Int
            // when navigating to details_screen/{scoreIndex},
            // Navigation knows how to parse it and makes it available in backStackEntry.arguments.
            arguments = listOf(
                navArgument(Screen.Details.ARG_SCORE_INDEX) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // Read index from navigation arguments and get the corresponding Score object from the ScoreViewModel
            val scoreIndex = backStackEntry.arguments?.getInt(Screen.Details.ARG_SCORE_INDEX) ?: -1
            val selectedScore = scoreViewModel.getPlayedGamesSequence().getOrNull(scoreIndex)

            if (selectedScore != null) {
                DetailScreen(
                    modifier = modifier,
                    score = selectedScore
                )
            } else {
                // Fallback in case an invalid index is passed in the route
                Text("Game not found")
            }
        }
    }
}
