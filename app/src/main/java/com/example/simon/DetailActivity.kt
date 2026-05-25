package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailScreen(modifier: Modifier = Modifier, score: Score) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeDetailLayout(modifier, score)
        }
        else -> {
            PortraitDetailLayout(modifier, score)
        }
    }
}

@Composable
fun LandscapeDetailLayout(modifier: Modifier, score: Score) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Title(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.game_details_text_detail_screen)
        )
        Text(
            text = stringResource(R.string.max_correct_sequence_detail_screen, score.maxCorrectSequence),
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.error_position_text_detail_screen, score.errorPosition),
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            val maxBoxHeight = maxHeight
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SequenceBox(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = stringResource(R.string.game_sequence_title_detail_screen),
                    sequence = score.playedGameSequence,
                    maxHeight = maxBoxHeight
                )
                SequenceBox(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = stringResource(R.string.user_sequence_title_detail_screen),
                    sequence = score.playedUserSequence,
                    maxHeight = maxBoxHeight
                )
            }
        }
    }
}

@Composable
fun PortraitDetailLayout(modifier: Modifier, score: Score) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Title(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.game_details_text_detail_screen)
        )
        Text(
            text = stringResource(R.string.max_correct_sequence_detail_screen, score.maxCorrectSequence),
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.error_position_text_detail_screen, score.errorPosition),
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp),
        ) {
            val maxBoxHeight = maxHeight / 2
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SequenceBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    title = stringResource(R.string.game_sequence_title_detail_screen),
                    sequence = score.playedGameSequence,
                    maxHeight = maxBoxHeight
                )
                SequenceBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    title = stringResource(R.string.user_sequence_title_detail_screen),
                    sequence = score.playedUserSequence,
                    maxHeight = maxBoxHeight
                )
            }
        }
    }
}

@Composable
private fun SequenceBox(
    modifier: Modifier,
    title: String,
    sequence: List<Char>,
    maxHeight: androidx.compose.ui.unit.Dp
) {
    val scrollState = rememberScrollState()
    val showProgress by remember {
        derivedStateOf { scrollState.maxValue > 0 }
    }
    Column(modifier = modifier) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
                .padding(8.dp)
                .heightIn(max = maxHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = sequence.joinToString(separator = ", "),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (showProgress) {
                LinearProgressIndicator(
                    progress = {
                        val max = scrollState.maxValue
                        if (max == 0) 0f else (scrollState.value.toFloat() / max).coerceIn(0f, 1f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}