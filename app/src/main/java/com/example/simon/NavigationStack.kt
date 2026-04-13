package com.example.simon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationStack(modifier: Modifier = Modifier) {
    // Create a NavController to manage navigation between screens
    // Create ViewModel instances for both GameScreen and ScoreScreen to safe state across navigation
    val navController = rememberNavController()
    val gameViewModel : GameViewModel = viewModel()
    val scoreViewModel : ScoreViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Game.route) {
        composable(route = Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                modifier = modifier,
                // onNavigateToScore is a lambda function that will be called when the user finishes a game and wants to navigate to the ScoreScreen
                onNavigateToScore = {
                    scoreViewModel.addPlayedGameSequence(gameViewModel.getUserSequence())
                    gameViewModel.clearUserSequence()
                    navController.navigate(Screen.Score.route)
                }
            )
        }
        composable(
            route = Screen.Score.route,
        ) {
            ScoreScreen(
                modifier = modifier,
                viewModel = scoreViewModel
            )
        }
    }
}