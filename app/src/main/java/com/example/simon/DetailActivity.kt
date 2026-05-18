package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    DetailsContent(modifier = modifier, score = score)
}

@Composable
fun PortraitDetailLayout(modifier: Modifier, score: Score) {
    DetailsContent(modifier = modifier, score = score)
}

@Composable
private fun DetailsContent(modifier: Modifier, score: Score) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Title(modifier = Modifier.fillMaxWidth(), text = "Game Details")
        Text(
            text = "Max correct sequence: ${score.maxCorrectSequence}",
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Error position: ${score.errorPosition}",
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Game sequence: ${score.playedGameSequence.joinToString(separator = ", ")}",
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "User sequence: ${score.playedUserSequence.joinToString(separator = ", ")}",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
