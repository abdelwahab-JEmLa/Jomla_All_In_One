package com.example.clientjetpack.ID1.Test.Fragment.Passive

import java.util.Calendar

fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val date = "${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')

        val time = "${calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:" +
                "${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}:" +
                calendar.get(Calendar.SECOND).toString().padStart(2, '0')

        return Pair(date, time)
    }
