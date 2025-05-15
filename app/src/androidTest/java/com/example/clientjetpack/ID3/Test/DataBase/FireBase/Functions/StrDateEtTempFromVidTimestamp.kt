package com.example.clientjetpack.ID3.Test.DataBase.FireBase.Functions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function to format timestamp to readable date and time
fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}
