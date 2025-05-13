package com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test.Z.Function

import java.util.Calendar

fun createTimestamp(year: Int = 2025, month: Int=5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
