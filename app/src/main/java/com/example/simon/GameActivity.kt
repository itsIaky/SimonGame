package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

// ViewModel should survive to screen rotations
class GameViewModel : ViewModel() {
    // This holds the user input sequence
    private val userSequence = mutableStateListOf<Char>()

    // This should be generated randomly in a real game
    // The sequence can:
    // contain duplicates
    // be of any length (usually the sequence gets longer with each round?? max?? min??)
    // regenerated only when in the ScoreActivity the back_button is pressed
    private val gameSequence = listOf('R', 'G', 'B', 'M', 'Y', 'C')

    fun clearUserSequence() {
        userSequence.clear()
    }

    fun addToUserSequence(color: Char) {
        userSequence.add(color)
    }

    fun getUserSequence(): List<Char> {
        return userSequence.toList()
    }

    fun finishGame() {
        // Logic to finish the game, e.g., check sequence, update score, switch screen, etc.
    }
}

// This is the main composable function for the game screen,
// which decides which layout to show based on the device orientation
@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel, onNavigateToScore: () -> Unit) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(modifier, viewModel, onNavigateToScore)
        }
        else -> {
            PortraitLayout(modifier, viewModel, onNavigateToScore)
        }
    }
}

@Composable
fun PortraitLayout(modifier: Modifier = Modifier, viewModel: GameViewModel, onNavigateToScore: () -> Unit) {
    Column(modifier = modifier.fillMaxSize()) {
        Title(modifier = Modifier.wrapContentHeight().align(Alignment.CenterHorizontally), text = stringResource(R.string.app_name))
        GameMatrix(Modifier.weight(4f), viewModel)
        SequenceDisplay(Modifier.weight(1f), viewModel)
        GameButtons(Modifier.wrapContentHeight(), viewModel, onNavigateToScore)
    }
}

@Composable
fun LandscapeLayout(modifier: Modifier = Modifier, viewModel: GameViewModel, onNavigateToScore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Title(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .wrapContentHeight(),
            text = stringResource(R.string.app_name)
        )

        Row(
            modifier = modifier
                .fillMaxWidth(),
        ) {
            // Left: GameMatrix takes full height
            GameMatrix(Modifier.weight(1f), viewModel)

            // Right: SequenceDisplay on top, GameButtons at bottom
            Column(modifier = Modifier.weight(1f)) {
                SequenceDisplay(Modifier.weight(1f), viewModel)
                GameButtons(Modifier.wrapContentHeight(), viewModel, onNavigateToScore)
            }
        }
    }
}

// This is the function that creates the matrix of colored buttons
@Composable
fun GameMatrix(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.weight(1f)) {
            ColorButton(Color.Red, 'R', Modifier.weight(1f), viewModel)
            ColorButton(Color.Green, 'G', Modifier.weight(1f), viewModel)
        }
        Row(modifier = Modifier.weight(1f)) {
            ColorButton(Color.Blue, 'B', Modifier.weight(1f), viewModel)
            ColorButton(Color.Magenta, 'M', Modifier.weight(1f), viewModel)
        }
        Row(modifier = Modifier.weight(1f)) {
            ColorButton(Color.Yellow, 'Y', Modifier.weight(1f), viewModel)
            ColorButton(Color.Cyan, 'C', Modifier.weight(1f), viewModel)
        }
    }
}

// This is the function that creates a colored button with the specified color and character
// This is then used in the matrix of buttons
// When the button is pressed, it adds the corresponding character to the user sequence in the GameViewModel
@Composable
fun ColorButton(color: Color, char : Char , modifier: Modifier, viewModel: GameViewModel) {
    Button(
        onClick = {
            viewModel.addToUserSequence(char)
        },
        colors = ButtonDefaults.buttonColors( color),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {}
}

@Composable
fun SequenceDisplay(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    // creates/keeps scroll state across recomposition
    val scrollState = rememberScrollState()
    // derived state to determine if we should show the progress bar (only if content is scrollable)
    val showProgress by remember {
        derivedStateOf { scrollState.maxValue > 0 }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sequence_text, viewModel.getUserSequence().joinToString(separator = ", ")),
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),
        )

        if (showProgress) {
            // progress value is computed in a lambda (better for frequently changing scroll values)
            // and constrained between [0f, 1f]
            LinearProgressIndicator(
                progress = {
                    val max = scrollState.maxValue
                    if (max == 0) 0f
                    else (scrollState.value.toFloat() / max).coerceIn(0f, 1f) // computes normalized progress between 0 and 1
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
            )
        }
    }
}

// This is the function that creates the buttons at the bottom of the screen (Clear and Finish Game)
// onNavigateToScore is a lambda that should be called when the Finish Game button is pressed, to navigate to the ScoreActivity
// Passing the user sequence to the ScoreViewModel and clearing the user sequence in the GameViewModel
@Composable
fun GameButtons(modifier: Modifier = Modifier, viewModel: GameViewModel, onNavigateToScore: () -> Unit) {
    Row(modifier = modifier.padding(16.dp)) {
        Button(onClick = {
            viewModel.clearUserSequence()
        }, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(stringResource(R.string.clear_button))
        }
        Button(onClick = {
            viewModel.finishGame()
            onNavigateToScore()
        }, modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(stringResource(R.string.finish_game_button))
        }
    }
}