package com.example.clientjetpack.Functions

/**
 * Helper function to check if two timestamps belong to the same day
 */
 fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
            cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH)
}
