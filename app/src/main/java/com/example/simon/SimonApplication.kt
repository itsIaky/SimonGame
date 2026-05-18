package com.example.simon

import android.app.Application

// app-level container for shared singletons (one shared instance of an object for the whole app lifecycle)
// used across screens.
class SimonApplication : Application() {
    // lazily creates one repository backed by the app-wide room database instance.
    // (lazy in Kotlin means, create the object only the first time it is accessed, then reuse that same object)
    val scoreRepository: ScoreRepository by lazy {
        ScoreRepository(
            scoreDao = SimonDatabase.getInstance(this).scoreDao()
        )
    }
}
