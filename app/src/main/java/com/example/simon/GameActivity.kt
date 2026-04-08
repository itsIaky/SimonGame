package com.example.simon

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.simon.ui.theme.SimonTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

class GameActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val systemLanguage = Locale.getDefault().language
        val languageCode = if (systemLanguage == "it") "it" else "en"

        val locale = Locale.forLanguageTag(languageCode)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimonTheme {
                val viewModel: GameViewModel = viewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ViewModel should survive to screen rotations
class GameViewModel : ViewModel() {
    // This holds the user input sequence
    val userSequence = mutableStateListOf<Char>()

    // This should be generated randomly in a real game
    // The sequence can:
    // contain duplicates
    // be of any length (usually the sequence gets longer with each round?? max?? min??)
    // regenerated only when in the ScoreActivity the back_button is pressed
    val gameSequence = listOf('R', 'G', 'B', 'M', 'Y', 'C')

    fun clearUserSequence() {
        userSequence.clear()
    }

    fun addToUserSequence(color: Char) {
        userSequence.add(color)
    }

    fun finishGame() {
        // Logic to finish the game, e.g., check sequence, update score, switch screen, etc.
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(modifier, viewModel)
        }
        else -> {
            PortraitLayout(modifier, viewModel)
        }
    }
}

@Composable
fun PortraitLayout(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    Column(modifier = modifier.fillMaxSize()) {
        GameMatrix(Modifier.weight(4f), viewModel)
        SequenceDisplay(Modifier.weight(1f), viewModel)
        GameButtons(Modifier.wrapContentHeight(), viewModel)
    }
}

@Composable
fun LandscapeLayout(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    Row(modifier = modifier.fillMaxSize()) {
        // Left: GameMatrix takes full height
        GameMatrix(Modifier.weight(1f), viewModel)
        
        // Right: SequenceDisplay on top, GameButtons at bottom
        Column(modifier = Modifier.weight(1f)) {
            SequenceDisplay(Modifier.weight(1f), viewModel)
            GameButtons(Modifier.wrapContentHeight(), viewModel)
        }
    }
}

@Composable
fun GameMatrix(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
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
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        // NEED TO BE MULTILINE
        // NO EDITABLE
        Text(
            text = stringResource(R.string.sequence_text, viewModel.userSequence.joinToString(separator = ", ")),
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),

            // NEED to choose if --> NEED TO ASK
            // allow infinite text with scroll
            // or limit lines and truncate with ellipsis
            //overflow = TextOverflow.Ellipsis,
            //maxLines = 3
        )

        // Show a progress bar --> NEED TO ASK IF IT'S OK
        if (scrollState.maxValue > 0) {
            val progress = (scrollState.value.toFloat() / scrollState.maxValue).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
fun GameButtons(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    Row(modifier = modifier.padding(16.dp)) {
        Button(onClick = {
            viewModel.clearUserSequence()
        }, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(stringResource(R.string.clear_button))
        }
        Button(onClick = {

        }, modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(stringResource(R.string.finish_game_button))
        }
    }
}