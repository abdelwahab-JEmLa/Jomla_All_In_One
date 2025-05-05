package com.example.clientjetpack.Logs.Functions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper function to format timestamp to time (HH:mm)
 */
 fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
