package com.example.simon

import android.app.Application

class SimonApplication : Application() {
    val scoreRepository: ScoreRepository by lazy {
        ScoreRepository(
            scoreDao = SimonDatabase.getInstance(this).scoreDao()
        )
    }
}
