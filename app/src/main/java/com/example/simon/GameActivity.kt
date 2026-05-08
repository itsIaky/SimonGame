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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.min

// ViewModel should survive to screen rotations
class GameViewModel : ViewModel() {
    // This holds the user input sequence
    private val userSequence = mutableStateListOf<Char>()

    private var currentStep = 0

    fun getCurrentStep(): Int {
        return currentStep
    }

    fun resetStep() {
        currentStep = 0
    }

    fun increseStep() {
        currentStep++
    }

    // This should be generated randomly in a real game
    // The sequence can:
    // contain duplicates
    // be of any length (usually the sequence gets longer with each round?? max?? min??)
    // regenerated only when in the ScoreActivity the back_button is pressed
    private val gameSequence = mutableStateListOf<Char>()

    fun generateGameCharacter(): Char {
        return when ((1..6).random()) {
            1 -> 'R'
            2 -> 'G'
            3 -> 'B'
            4 -> 'M'
            5 -> 'Y'
            else -> 'C'
        }
    }

    fun clearGameSequence() {
        gameSequence.clear()
    }

    fun addToGameSequence(gameCharacter: Char) {
        gameSequence.add(gameCharacter)
    }

    fun getGameSequence(): List<Char> {
        return gameSequence.toList()
    }

    fun clearUserSequence() {
        userSequence.clear()
    }

    fun addToUserSequence(color: Char) {
        userSequence.add(color)
    }

    fun getUserSequence(): List<Char> {
        return userSequence.toList()
    }

    fun finishGame(): Boolean {
        // Logic to finish the game, e.g., check sequence, update score, switch screen, etc.
        return userSequence == gameSequence
    }

    fun checkUserSequence(character: Char): Boolean {
//        // Check if the user sequence is correct so far
//        for (i in userSequence.indices) {
//            if (userSequence[i] != gameSequence[i]) {
//                return false
//            }
//        }
//        return true
        return character == gameSequence[currentStep]
    }

    // mutableStateOf is needed to trigger recomposition when the value changes
    var isGameActive by mutableStateOf(false)
    var isGamePaused by mutableStateOf(false)
    var isPresentingSequence by mutableStateOf(false)
    var failed by mutableStateOf(false)

    fun nextRound() {
        gameSequence.add(generateGameCharacter())
        presentSequence()
    }

    var highlightedChar by mutableStateOf<Char?>(null)
    private val pauseFlow = MutableStateFlow(false)
    private var playbackJob: Job? = null

    fun pauseGame() {
        isGamePaused = true
        pauseFlow.value = true
    }

    fun resumeGame() {
        isGamePaused = false
        pauseFlow.value = false
    }

    fun stopPresentation() {
        playbackJob?.cancel()
        highlightedChar = null
        isPresentingSequence = false
    }

    fun presentSequence() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            isPresentingSequence = true
            try {
                for (c in gameSequence) {
                    awaitIfPaused()

                    highlightedChar = c
                    // play sound for c here
                    pauseAwareDelay(450)

                    highlightedChar = null
                    pauseAwareDelay(150)
                }
            } finally {
                highlightedChar = null
                isPresentingSequence = false
            }
        }
    }

    private suspend fun awaitIfPaused() {
        pauseFlow.filter { paused -> !paused }.first()
    }

    private suspend fun pauseAwareDelay(totalMs: Long) {
        var remaining = totalMs
        while (remaining > 0) {
            awaitIfPaused()
            val step = min(remaining, 16L)
            delay(step)
            if (!pauseFlow.value) remaining -= step
        }
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
    val isLit = viewModel.highlightedChar == char
    val shownColor = if (isLit) lerp(color, Color.White, 0.45f) else color

    Button(
        onClick = {
            if (!viewModel.isGameActive || viewModel.failed || viewModel.isGamePaused || viewModel.isPresentingSequence) {
                // If the game is not active, has failed, is paused, or is presenting the sequence, ignore button presses
                return@Button
            }
            viewModel.addToUserSequence(char)
            if (viewModel.checkUserSequence(char)) {
                    viewModel.increseStep()
                if (viewModel.getUserSequence().size == viewModel.getGameSequence().size) {
                    // User completed the sequence correctly, move to next round
                    viewModel.resetStep()
                    viewModel.clearUserSequence()
                    viewModel.nextRound()
                }
            } else {
                // User made a mistake, end the game
                viewModel.isGameActive = false
                viewModel.failed = true
                // TODO: Show a message to the user that they lost and navigate to the score screen
            }
        },
        //enabled = viewModel.isGameActive && !viewModel.faield && !viewModel.isGamePaused && !viewModel.isPresentingSequence,
        colors = ButtonDefaults.buttonColors( shownColor),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    ) {

    }
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
        Button(
            onClick = {
                viewModel.resetStep()
                viewModel.clearUserSequence()
                viewModel.clearGameSequence()
                viewModel.isGameActive = true
                viewModel.isPresentingSequence = true
                viewModel.failed = false
                viewModel.isGamePaused = false
                viewModel.nextRound() // starts the game with the first round
            },
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            enabled = !viewModel.isGameActive && !viewModel.failed

        ) {
            Text(stringResource(R.string.start_game_button))
        }

        Button(
            onClick = {
                if (viewModel.isGamePaused) viewModel.resumeGame() else viewModel.pauseGame()
            },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            enabled = viewModel.isGameActive && !viewModel.failed && viewModel.isPresentingSequence
        ) {
            if(viewModel.isGamePaused)
                Text(stringResource(R.string.resume_button))
            else
                Text(stringResource(R.string.pause_button))
        }

        Button(
            onClick = {
                onNavigateToScore()
            },
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            enabled = viewModel.isGameActive && !viewModel.failed
        ) {
            Text(stringResource(R.string.finish_game_button))
        }
    }
}