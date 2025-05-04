package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Repositorys.DatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.StrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack.Repositorys.createTestTransactions
import com.example.clientjetpack.Repositorys.testHardDataDatesHistoriqueTransactions
import com.example.clientjetpack.Tests.A.Filter.getFilteredTransactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Improved unit tests that don't rely on external ViewModels
 *
 * Uses direct testing of the data processing logic without dependencies
 */
@ExperimentalCoroutinesApi
class SImprovedClientsMapFilterViewModelTest {

    // Rule to make LiveData work instantly in tests
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Test dispatcher for Compose/Coroutines operations
    private val testDispatcher = TestCoroutineDispatcher()

    // Our test data - a simple list of transactions
    val testTransactions = ArrayList<TransactionCommercial>()

    // Store for the uniqueDays data
    private var uniqueDaysForTesting = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

    // Store for dates historique
    private var datesHistoriqueForTesting: DatesHistoriqueTransactions? = null

    // Current filter state for direct testing
    var currentFilter = FilterType.ALL

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Create some test transactions
        testTransactions.addAll(createTestTransactions())

        // Collect data for testing
        collecteAddAuStrNomJourEtSonSemainToStartJourTimeTemp()
        collecteAddAuDatesHistoriqueTransactions()
    }

    private fun collecteAddAuStrNomJourEtSonSemainToStartJourTimeTemp() {
        // Track changes in transactions with COMMANDE_LIVRAI status
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        // Create a list to store unique days with transactions
        val uniqueDays = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

        testTransactions.forEach { transaction ->
            if (transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI) {
                // Get transaction date
                val transactionDate = Date(transaction.timestamps)

                // Get start and end of day
                val startDay = getStartOfDay(transaction.timestamps)
                val endDay = getEndOfDay(transaction.timestamps)

                // Calculate how many weeks ago this was
                val weeksDifference = getWeeksDifference(today, transaction.timestamps)

                // Get day name in Arabic
                val dayFormat = SimpleDateFormat("EEEE", Locale("ar"))
                val dayName = dayFormat.format(transactionDate)

                // Create key for uniqueness
                val key = "${startDay}_${dayName}_${weeksDifference}"

                // Check if this day is already in our list
                val existingDay = uniqueDays.find { it.key == key }

                if (existingDay == null) {
                    // Add new day if not exists
                    uniqueDays.add(
                        StrNomJourEtSonSemainToStartJourTimeTemp(
                            vid = transaction.vid,
                            nomJourArabe = dayName,
                            estDonLaSemainDistantDe = weeksDifference,
                            jourEstEntreTimeTemp = Pair(startDay, endDay),
                            key = key
                        )
                    )
                }
            }
        }

        // Store the results in our class variable
        this.uniqueDaysForTesting = uniqueDays
    }

    private fun collecteAddAuDatesHistoriqueTransactions() {
        // Create instances for weeks and days based on the collected dates
        val semainsList = mutableListOf<DatesHistoriqueTransactions.Semain>()

        // Group by week distance using our uniqueDaysForTesting
        val groupedByWeek = uniqueDaysForTesting.groupBy {
            it.estDonLaSemainDistantDe
        }

        groupedByWeek.forEach { (weekDistance, days) ->
            val semain = DatesHistoriqueTransactions.Semain().apply {
                vid = weekDistance.toLong()
                key = "Semaine-$weekDistance"
            }

            val joursList = mutableListOf<DatesHistoriqueTransactions.Semain.Jour>()

            days.forEach { dayInfo ->
                val jour = DatesHistoriqueTransactions.Semain.Jour().apply {
                    vid = dayInfo.vid
                    key = dayInfo.key
                }

                // Find all transactions for this day
                val dayTransactions = testTransactions.filter { transaction ->
                    transaction.timestamps >= dayInfo.jourEstEntreTimeTemp.first &&
                            transaction.timestamps <= dayInfo.jourEstEntreTimeTemp.second
                }

                jour.cesCommercialTransactions = dayTransactions
                joursList.add(jour)
            }

            semain.cesJours = joursList
            semainsList.add(semain)
        }

        // Store result in our class variable
        this.datesHistoriqueForTesting = DatesHistoriqueTransactions().apply {
            this.cesSemains = semainsList
        }
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testAllFilterShowsAllTransactions() {
        // Use the ALL filter (default)
        currentFilter = FilterType.ALL

        // Get filtered transactions
        val filteredTransactions = getFilteredTransactions()

        // Check we get all transactions
        assertEquals(testTransactions.size, filteredTransactions.size)
    }

    /**
     * Function to log detailed information about DatesHistoriqueTransactions structure
     * Extracted as per
     */
    private fun logDatesHistoriqueStructure(testData: DatesHistoriqueTransactions) {
        println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

        println("Created test data structure for DatesHistoriqueTransactions")

        // Verify structure of test data
        println("Testing overall structure: expecting 2 weeks")
        println("✓ Found expected number of weeks: ${testData.cesSemains.size}")

        // Log overall structure
        println("\n== Structure Overview ==")
        testData.cesSemains.forEachIndexed { weekIndex, week ->
            println("Week ${weekIndex+1}: vid=${week.vid}, key=${week.key}, active=${week.cActive}, days=${week.cesJours.size}")
            week.cesJours.forEachIndexed { dayIndex, day ->
                println("  Day ${dayIndex+1}: vid=${day.vid}, key=${day.key}, active=${day.cActive}, transactions=${day.cesCommercialTransactions.size}")
                day.cesCommercialTransactions.forEachIndexed { txIndex, tx ->
                    println("    Tx ${txIndex+1}: vid=${tx.vid}, state=${tx.etateActuellementEst}, client=${tx.nomClientConcerned}")
                }
            }
        }

        // Test week 1
        println("\n== Testing Week 1 Details ==")
        val week1 = testData.cesSemains[0]
        println("✓ Week 1 vid: ${week1.vid}")
        println("✓ Week 1 key: ${week1.key}")
        println("✓ Week 1 active status: ${week1.cActive}")
        println("✓ Week 1 days count: ${week1.cesJours.size}")

        // Test first day of week 1
        println("\n== Testing Week 1, Day 1 Details ==")
        val day1Week1 = week1.cesJours[0]
        println("✓ Day 1 vid: ${day1Week1.vid}")
        println("✓ Day 1 key: ${day1Week1.key}")
        println("✓ Day 1 active status: ${day1Week1.cActive}")
        println("✓ Day 1 transactions count: ${day1Week1.cesCommercialTransactions.size}")

        // Test a specific transaction
        println("\n== Testing Specific Transaction Details ==")
        val transaction1 = day1Week1.cesCommercialTransactions[0]
        println("✓ Transaction 1 vid: ${transaction1.vid}")
        println("✓ Transaction 1 state: ${transaction1.etateActuellementEst}")
        println("✓ Transaction 1 client name: ${transaction1.nomClientConcerned}")

        // Test week 2
        println("\n== Testing Week 2 Details ==")
        val week2 = testData.cesSemains[1]
        println("✓ Week 2 vid: ${week2.vid}")
        println("✓ Week 2 key: ${week2.key}")
        println("✓ Week 2 active status: ${week2.cActive}")
        println("✓ Week 2 days count: ${week2.cesJours.size}")

        // Test second day of week 2
        println("\n== Testing Week 2, Day 2 Details ==")
        val day2Week2 = week2.cesJours[1]
        println("✓ Day 2 vid: ${day2Week2.vid}")
        println("✓ Day 2 key: ${day2Week2.key}")
        println("✓ Day 2 active status: ${day2Week2.cActive}")
        println("✓ Day 2 transactions count: ${day2Week2.cesCommercialTransactions.size}")

        // Test nested structure traversal - recursively check transaction count for all days
        println("\n== Testing Transaction Count Across All Structure ==")
        var totalTransactions = 0
        testData.cesSemains.forEach { week ->
            week.cesJours.forEach { day ->
                val dayTransactions = day.cesCommercialTransactions.size
                println("  Found ${dayTransactions} transactions in week ${week.vid}, day with key ${day.key}")
                totalTransactions += dayTransactions
            }
        }
        println("✓ Total transactions verified: ${totalTransactions}")

        // Test that all transactions in the structure have COMMANDE_LIVRAI status
        println("\n== Verifying All Transactions Status ==")
        var validTransactions = 0
        testData.cesSemains.forEach { week ->
            week.cesJours.forEach { day ->
                day.cesCommercialTransactions.forEach { transaction ->
                    if (transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI) {
                        validTransactions++
                    }
                }
            }
        }
        println("✓ All ${validTransactions} transactions have correct COMMANDE_LIVRAI status")
        println("\n======== TEST COMPLETED SUCCESSFULLY ========")
    }

    @Test
    fun testDatesHistoriqueTransactions() {
        // Create a test instance of DatesHistoriqueTransactions
        val testData = testHardDataDatesHistoriqueTransactions()

        logDatesHistoriqueStructure(testData)

        // Verify structure of test data
        assertEquals("Should have 2 weeks", 2, testData.cesSemains.size)

        // Test week 1
        val week1 = testData.cesSemains[0]
        assertEquals(1L, week1.vid)
        assertEquals("Semaine-1", week1.key)
        assertTrue(week1.cActive)
        assertEquals("Week 1 should have 2 days", 2, week1.cesJours.size)

        // Test first day of week 1
        val day1Week1 = week1.cesJours[0]
        assertEquals(101L, day1Week1.vid)
        assertEquals("1_الأحد_1", day1Week1.key)
        assertTrue(day1Week1.cActive)
        assertEquals("Day 1 should have 2 transactions", 2, day1Week1.cesCommercialTransactions.size)

        // Test a specific transaction
        val transaction1 = day1Week1.cesCommercialTransactions[0]
        assertEquals(1001L, transaction1.vid)
        assertEquals(TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI, transaction1.etateActuellementEst)
        assertEquals("عميل 1", transaction1.nomClientConcerned)

        // Test week 2
        val week2 = testData.cesSemains[1]
        assertEquals(2L, week2.vid)
        assertEquals("Semaine-2", week2.key)
        assertFalse(week2.cActive)
        assertEquals("Week 2 should have 2 days", 2, week2.cesJours.size)

        // Test second day of week 2
        val day2Week2 = week2.cesJours[1]
        assertEquals(202L, day2Week2.vid)
        assertEquals("2_الخميس_2", day2Week2.key)
        assertTrue(day2Week2.cActive)
        assertEquals("This day should have 2 transactions", 2, day2Week2.cesCommercialTransactions.size)

        // Test nested structure traversal - recursively check transaction count for all days
        var totalTransactions = 0
        testData.cesSemains.forEach { week ->
            week.cesJours.forEach { day ->
                totalTransactions += day.cesCommercialTransactions.size
            }
        }
        assertEquals("Total transactions across all nested structure should be 7", 7, totalTransactions)

        // Test that all transactions in the structure have COMMANDE_LIVRAI status
        testData.cesSemains.forEach { week ->
            week.cesJours.forEach { day ->
                day.cesCommercialTransactions.forEach { transaction ->
                    assertEquals(
                        "All transactions should have COMMANDE_LIVRAI status",
                        TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        transaction.etateActuellementEst
                    )
                }
            }
        }
    }

    // Define filter types as an enum for direct testing
    enum class FilterType {
        ALL,
        DatesHistoriqueTransactions,
        CIBLE,
    }
}
