package com.example.clientjetpack.ID4.Test.Archive

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Add function to format timestamp
fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}
