package com.example.clientjetpack.ID3.Test.DataBase.FireBase.Functions

// Extension function to round double to specified decimal places
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
