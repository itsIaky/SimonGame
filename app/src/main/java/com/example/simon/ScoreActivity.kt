package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

class ScoreViewModel : ViewModel() {
    private val playedGamesSequence = mutableStateListOf<Score>()

    fun addPlayedGameSequence(gameScore: Score) {
        playedGamesSequence.add(gameScore)
    }

    fun getPlayedGamesSequence(): List<Score> {
        return playedGamesSequence.toList()
    }
}

@Composable
fun ScoreScreen(modifier: Modifier = Modifier, viewModel: ScoreViewModel, onNavigateToGame: () -> Unit = {}) {
        val configuration = LocalConfiguration.current

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeScoreLayout(modifier, viewModel, onNavigateToGame)
            }
            else -> {
                PortraitScoreLayout(modifier, viewModel, onNavigateToGame)
            }
        }
}

@Composable
fun PortraitScoreLayout(modifier: Modifier, scoreViewModel: ScoreViewModel, onNavigateToGame: () -> Unit = {}) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(modifier = Modifier, text = "Played Games")
        ScoreList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            scoreViewModel = scoreViewModel,
        )
        Button(
            modifier = Modifier,
            onClick = {
                onNavigateToGame()
            }
        ) {
            Text(stringResource(R.string.play_button))
        }
    }
}

@Composable
fun LandscapeScoreLayout(modifier: Modifier, scoreViewModel: ScoreViewModel, onNavigateToGame: () -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Title(modifier = Modifier, text = "Played Games")
        ScoreList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            scoreViewModel = scoreViewModel,
        )
        Button(
            modifier = Modifier,
            onClick = {
                onNavigateToGame()
            }
        ) {
            Text(stringResource(R.string.play_button))
        }
    }
}

// This composable is used to display the title of the screen
// used in both game and score screen
@Composable
fun Title(modifier: Modifier, text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun ScoreList(modifier: Modifier, scoreViewModel: ScoreViewModel) {
    val scrollState = rememberScrollState()
    val showProgress by remember {
        derivedStateOf { scrollState.maxValue > 0 }
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        scoreViewModel.getPlayedGamesSequence().forEach { gameSequence ->
            PlayedGameText(
                modifier = Modifier,
                maxScore = gameSequence.getMaxCorrectSequence(),
                gameSequence = gameSequence.getPlayedGamesSequence(),
                errorPosition = gameSequence.getErrorPosition()
            )
        }
    }

    if (showProgress) {
        LinearProgressIndicator(
            progress = {
                val max = scrollState.maxValue
                if (max == 0) 0f
                else (scrollState.value.toFloat() / max).coerceIn(0f, 1f) // computes normalized progress between 0 and 1
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .padding(bottom = 16.dp),
        )
    }
}

// Display a card with the details of each played game
// displaying:
// 1) the number of squares pressed
// 2) the sequence of colors pressed (truncated with dots if too long)
@Composable
fun PlayedGameText(modifier: Modifier, maxScore : Int, gameSequence: List<Char>, errorPosition: Int) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 24.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$maxScore",
                modifier = modifier
                    .wrapContentSize(Alignment.CenterStart)
                    .padding(vertical = 8.dp)
                    .weight(1f)
            )
            VerticalDivider(
                thickness = 1.dp,

            )
            /*
            Text(
                text = gameSequence.joinToString(", "),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .weight(9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
             */
            TwoColorText(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .weight(9f),
                gameSequenceString = gameSequence.joinToString(", "),
                errorPosition = errorPosition
            )
        }
    }
}

@Composable
fun TwoColorText(modifier: Modifier,
                 gameSequenceString: String,
                 errorPosition: Int
) {
    val split = errorPosition.coerceIn(0, gameSequenceString.length)

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(brush = SolidColor(Color.Black))) {
                append(gameSequenceString.take(split))      // first x chars
            }
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(gameSequenceString.drop(split))      // remaining chars
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}