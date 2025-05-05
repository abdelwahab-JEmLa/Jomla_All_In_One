package com.example.clientjetpack.Tests.B.Data

/*

@Test
fun testDatesHistoriqueTransactions() {
    val testData = datesHistoriqueForTesting

    SqlDatasDatesHistoriqueTransactionslog(testData)

    // Verify structure of test data
    assertEquals("Should have 1 weeks", 1, testData.cesSemainKeys.size)

    // Test week 1
    val week1 = testData.cesSemainKeys[0]
    assertEquals(1L, week1.vid)
    assertEquals("Semaine-1", week1.key)
}

@Test
fun testQueCeJoureAUnClientAbderrahmane() {
    // Check if client Abderrahmane exists in today's transactions
    val hasClientAbderrahmane = checkClientExistsInCurrentDay(datesHistoriqueForTesting, "Abderrahmane")

    // Assert that Abderrahmane exists in today's transactions
    assertEquals("Should have a client named Abderrahmane", true, hasClientAbderrahmane)
}

@Test
fun testQueCeJoureAUnClientHoussine() {
    val hasClientHoussine = testTransactions.any { transaction ->
        transaction.nomClientConcerned.contains("an")
    }

    assertTrue("Should ", hasClientHoussine)
}
/**
 * Tests if a specific client exists in today's transactions
 */
fun checkClientExistsInCurrentDay(
    datesHistorique: MapsIDSDatesHistoriqueTransactions,
    clientName: String
): Boolean {
    // Check if we have any weeks
    if (datesHistorique.cesSemainKeys.isEmpty()) {
        return false
    }

    // Find the current day's transactions (today)
    val currentDay = datesHistorique.cesSemainKeys.flatMap { semain ->
        semain.cesJourKeys
    }.find { jour ->
        // Find the most recent day (should be today based on our test data)
        val calendar = Calendar.getInstance()
        val todayStartTime = getStartOfDay(calendar.timeInMillis)
        val todayEndTime = getEndOfDay(calendar.timeInMillis)

        jour.cesCommercialTransactions.any { transaction ->
            transaction.timestamps in todayStartTime..todayEndTime
        }
    }

    // Check if we found the current day
    if (currentDay == null) {
        return false
    }

    // Check if the specified client exists in today's transactions
    return currentDay.cesCommercialTransactions.any { transaction ->
        transaction.nomClientConcerned == clientName
    }
}
                          */
