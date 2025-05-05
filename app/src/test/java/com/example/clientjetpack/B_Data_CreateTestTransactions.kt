package com.example.clientjetpack

import com.example.clientjetpack.Repositorys.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Converts a date/time string in format "dd/MM h.mmA" or "dd/MM h.mma"
 * (like "05/05 8.30PM" or "05/05 8.30pm") into a timestamp
 */
fun getFromeDayeStringTime(dateTimeStr: String): Long {
    try {
        // Handle variations in input format (PM/pm, with or without spaces)
        val cleanInput = dateTimeStr.trim().replace("\\s+".toRegex(), " ")
            .uppercase() // Standardize AM/PM to uppercase

        // Parse different potential formats
        val dateTimeParts = cleanInput.split(" ")
        if (dateTimeParts.size != 2) {
            throw ParseException("Invalid date/time format: $dateTimeStr", 0)
        }

        val datePart = dateTimeParts[0] // e.g., "05/05"
        var timePart = dateTimeParts[1] // e.g., "8.30PM"

        // Add current year to the date part since it's not specified in the input
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val fullDateStr = "$datePart/$currentYear"

        // Handle time format with period separator (8.30PM)
        timePart = timePart.replace(".", ":")

        // Extract AM/PM indicator
        val hasAmPm = timePart.endsWith("AM") || timePart.endsWith("PM")
        if (!hasAmPm) {
            throw ParseException("Time must include AM or PM: $timePart", 0)
        }

        // Create a complete date-time string
        val fullDateTime = "$fullDateStr $timePart"

        // Parse the date-time string
        val formatter = SimpleDateFormat("dd/MM/yyyy h:mma", Locale.US)
        val date = formatter.parse(fullDateTime) ?: throw ParseException("Failed to parse: $fullDateTime", 0)

        return date.time
    } catch (e: Exception) {
        // In case of any parsing errors, log and return current time as fallback
        println("Error parsing date/time string '$dateTimeStr': ${e.message}")
        return System.currentTimeMillis()
    }
}

fun B_Data_CreateTestTransactions(): List<D_Repo_TransactionCommercial> {
    val testTransactions = ArrayList<D_Repo_TransactionCommercial>()

    // Set timestamps for different days
    val calendar = Calendar.getInstance()

    // First transaction: today at 1 PM
    calendar.set(Calendar.HOUR_OF_DAY, 13) // 1 PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayTimestamp = calendar.timeInMillis

    // Second transaction: yesterday at 3 PM
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 15) // 3 PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val yesterdayTimestamp = calendar.timeInMillis

    // Create a timestamp for yesterday at 3:30 PM
    val calendar2 = Calendar.getInstance()
    calendar2.timeInMillis = yesterdayTimestamp
    calendar2.set(Calendar.MINUTE, 30)
    val yesterdayTimestamp330 = calendar2.timeInMillis

    calendar.add(Calendar.DAY_OF_MONTH, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 17) //  PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val yesterdayTimestamp17 = calendar.timeInMillis

    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 1L,
            clientAcheteurID = 1L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Abderrahmane",
            timestamps = todayTimestamp
        )
    )
    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 5L,
            clientAcheteurID = 1L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Abderrahmane",
            timestamps = getFromeDayeStringTime("05/05 8.30PM")
        )
    )

    // Add a CIBLE transaction for client 2
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 2L,
            clientAcheteurID = 2L,  // Set unique client ID
            etateActuellementEst = Type.Cible,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp
        )
    )
    // Add a CIBLE transaction for client 2
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 3L,
            clientAcheteurID = 2L,
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp330
        )
    )

    // Add a COMMANDE_LIVRAI transaction for client 3
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 4L,
            clientAcheteurID = 3L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Fares",
            timestamps = yesterdayTimestamp17
        )
    )

    return testTransactions
}
