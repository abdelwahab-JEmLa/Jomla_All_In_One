package com.example.clientjetpack.ID4.Test

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}
