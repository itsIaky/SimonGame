package com.example.simon

import androidx.room.TypeConverter

// converts unsupported room types to storable primitives and back
class ScoreConverters {
    // persist a sequence of color chars as a compact string (['R','G'] -> "RG")
    @TypeConverter
    fun fromCharList(chars: List<Char>): String {
        return chars.joinToString(separator = "")
    }

    // recreate the original sequence from the stored compact string
    @TypeConverter
    fun toCharList(charsString: String): List<Char> {
        return charsString.toList()
    }
}
