package com.example.simon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationStack(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val gameViewModel : GameViewModel = viewModel()
    val scoreViewModel : ScoreViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Game.route) {
        composable(route = Screen.Game.route) {
            GameScreen(
                navController = navController,
                viewModel = gameViewModel,
                modifier = modifier,
                sharedViewModel = scoreViewModel,
                onNavigateToScore = {
                    navController.navigate(Screen.Score.route)
                }
            )
        }
        composable(
            route = Screen.Score.route,
        ) {
            ScoreScreen(
                navController = navController,
                modifier = Modifier,
                viewModel = scoreViewModel)
        }
    }
}