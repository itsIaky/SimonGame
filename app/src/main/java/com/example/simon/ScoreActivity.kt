package com.example.simon

import android.app.Application
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreRepository = (application as SimonApplication).scoreRepository

    val playedGames: StateFlow<List<Score>> = scoreRepository.playedGames
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun observePlayedGameById(scoreId: Long): Flow<Score?> {
        return scoreRepository.observePlayedGameById(scoreId)
    }
}

@Composable
fun ScoreScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreViewModel,
    onNavigateToGame: () -> Unit = {},
    onNavigateToDetails: (Long) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val playedGames by viewModel.playedGames.collectAsState()

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeScoreLayout(modifier, playedGames, onNavigateToGame, onNavigateToDetails)
        }
        else -> {
            PortraitScoreLayout(modifier, playedGames, onNavigateToGame, onNavigateToDetails)
        }
    }
}

@Composable
fun PortraitScoreLayout(
    modifier: Modifier,
    playedGames: List<Score>,
    onNavigateToGame: () -> Unit = {},
    onNavigateToDetails: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(modifier = Modifier, text = stringResource(R.string.played_games_text_score_screen))
        ScoreList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            playedGames = playedGames,
            onNavigateToDetails = onNavigateToDetails
        )
        Button(
            modifier = Modifier,
            onClick = {
                onNavigateToGame()
            }
        ) {
            Text(stringResource(R.string.play_button_score_screen))
        }
    }
}

@Composable
fun LandscapeScoreLayout(
    modifier: Modifier,
    playedGames: List<Score>,
    onNavigateToGame: () -> Unit = {},
    onNavigateToDetails: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Title(modifier = Modifier, text = stringResource(R.string.played_games_text_score_screen))
        ScoreList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            playedGames = playedGames,
            onNavigateToDetails = onNavigateToDetails
        )
        Button(
            modifier = Modifier,
            onClick = {
                onNavigateToGame()
            }
        ) {
            Text(stringResource(R.string.play_button_score_screen))
        }
    }
}

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
fun ScoreList(modifier: Modifier, playedGames: List<Score>, onNavigateToDetails: (Long) -> Unit) {
    val scrollState = rememberScrollState()
    val showProgress by remember {
        derivedStateOf { scrollState.maxValue > 0 }
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        playedGames.forEach { score ->
            PlayedGameText(
                modifier = Modifier,
                maxScore = score.maxCorrectSequence,
                gameSequence = score.playedGameSequence,
                errorPosition = score.errorPosition,
                onClick = { onNavigateToDetails(score.id) }
            )
        }
    }

    if (showProgress) {
        LinearProgressIndicator(
            progress = {
                val max = scrollState.maxValue
                if (max == 0) 0f
                else (scrollState.value.toFloat() / max).coerceIn(0f, 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
fun PlayedGameText(
    modifier: Modifier,
    maxScore: Int,
    gameSequence: List<Char>,
    errorPosition: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 24.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        onClick = onClick
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
            VerticalDivider(thickness = 1.dp)
            TwoColorText(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .weight(9f),
                gameSequenceString = gameSequence,
                errorPosition = errorPosition
            )
        }
    }
}

@Composable
fun TwoColorText(
    modifier: Modifier,
    gameSequenceString: List<Char>,
    errorPosition: Int
) {
    val split = errorPosition.coerceIn(0, gameSequenceString.size)

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(gameSequenceString.take(split).joinToString(", "))
            }
            if (split > 0 && split < gameSequenceString.size) {
                append(", ")
            }
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(gameSequenceString.drop(split).joinToString(", "))
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}
