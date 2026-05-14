package com.example.simon

import androidx.room.TypeConverter

class ScoreConverters {
    @TypeConverter
    fun fromCharList(chars: List<Char>): String {
        return chars.joinToString(separator = "")
    }

    @TypeConverter
    fun toCharList(charsString: String): List<Char> {
        return charsString.toList()
    }
}
