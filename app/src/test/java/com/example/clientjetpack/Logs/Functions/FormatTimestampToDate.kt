package com.example.clientjetpack.Logs.Functions

// Helper function to format timestamp to readable date
fun formatTimestampToDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}
