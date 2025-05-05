package com.example.clientjetpack.Logs.Functions

/**
 * Helper function to check if two timestamps belong to the same week
 */
 fun belongsToSameWeek(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.WEEK_OF_YEAR) == cal2.get(java.util.Calendar.WEEK_OF_YEAR)
}
