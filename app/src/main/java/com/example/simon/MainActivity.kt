package com.example.simon

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.simon.ui.theme.SimonTheme
import java.util.Locale

// MainActivity is the entry point of the app
// overrides the attachBaseContext method to set the app's locale based on the system language (english is default).
// sets the content view to the NavigationStack composable
// which is responsible for handling the navigation between screens
class MainActivity : ComponentActivity() {
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationStack(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}