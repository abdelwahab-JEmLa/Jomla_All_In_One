package com.example.clientjetpack.Logs.Functions

import java.util.Calendar

/**
     * Normalizes a timestamp to midnight of the day (00:00:00.000)
     */
     fun normalizeToDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Normalizes a timestamp to the first day of the week containing the timestamp
     */
     fun normalizeToWeekStart(dayTimestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayTimestamp
            // Set to first day of week (usually Sunday or Monday depending on locale)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        return calendar.timeInMillis
    }
