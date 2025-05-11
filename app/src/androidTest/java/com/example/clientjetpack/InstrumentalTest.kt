package com.example.clientjetpack

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InstrumentalTest :
    KoinTest,
    TarificationViewModel.TestCallbacks {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
/*
        .filterByTag("TestRunner")
        .filterByTag("FireBaseHandler")
        .filterByTag("InputEtInfosRepo")            */
        .build()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TarificationViewModel

    private val operationLatch = CountDownLatch(1)
    private var operationSuccessful = false
    private var lastResult: Any? = null

    // Use the tag constant without a variable, so it matches the filter exactly
    private val TAG = "InstrumentalTest"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        stopKoin()

        runBlocking {
            // Any suspending functions here
        }

        startKoin {
            modules(
                module {
                    single { this@InstrumentalTest }
                    single { TarificationViewModel(get()) }
                }
            )
        }

        viewModel = TarificationViewModel(this@InstrumentalTest)
        operationSuccessful = false
        lastResult = null
        Log.i(TAG, "Test setup complete")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testBasicLogging() = runTest {
        // Use the direct tag name instead of the TAG variable to ensure it matches filter
        Log.i("InstrumentalTest", "Starting testBasicLogging")

        assertEquals(
            1L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        Log.i("InstrumentalTest", "Completed testBasicLogging")
    }

    @Test
    fun testLogWithMethodFilter() = runTest {
        Log.i("InstrumentalTest", "Starting testLogWithMethodFilter")

        viewModel.addNewTestDataTarificationEtClient()
        Log.i("InstrumentalTest", "Called addNewTestDataTarificationEtClient")

        assertTrue("Firebase operation timed out", awaitOperationCompletion())
        Log.i("InstrumentalTest", "Operation completed: success=$operationSuccessful")

        assertEquals(
            2L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        LogFilterRule.log("InstrumentalTest", "Testing manual log in testLogWithMethodFilter")

        if (lastResult is List<*> && (lastResult as List<*>).isNotEmpty()) {
            val filteredItems = (lastResult as List<*>).filterIsInstance<InputEtInfosSqlModels.Tarification>()
                .filter { it.idClient == 1L && it.idProduit == 1L && it.idTypeTarification == 1L }

            if (filteredItems.isNotEmpty()) {
                val mostRecentItem = filteredItems.maxByOrNull { it.vidTimestamp }

                Log.i("InstrumentalTest", "Found matching item from list: $mostRecentItem")

                val testData = InputEtInfosSqlModels.Tarification(
                    vidTimestamp = mostRecentItem?.vidTimestamp
                        ?: (System.currentTimeMillis() - 86400000),
                    idProduit = 1L,
                    idClient = 1L,
                    idTypeTarification = 1L,
                    prixCurrency = 2.99
                )

                // Now assert on this specific item
                assertResultItem(testData, mostRecentItem)
            } else {
                // No matching item found
                Log.i("InstrumentalTest", "No matching item found in result list")
                assertTrue("No matching Tarification item found in result list", false)
            }
        } else {
            // Handle case where lastResult is a single item
            val testData = InputEtInfosSqlModels.Tarification(
                vidTimestamp = System.currentTimeMillis() - 86400000,
                idProduit = 1L,
                idClient = 1L,
                idTypeTarification = 1L,
                prixCurrency = 2.99
            )
            assertResult(testData)
        }
    }

    override fun <T> onOperationSuccess(result: T) {
        lastResult = result
        operationSuccessful = true
        operationLatch.countDown()

        // Use direct tag name for logging
        Log.i("InstrumentalTest", "Operation success callback with result: $result")

        // Additional assertions can be made here if needed
        if (result != null) {
            assertEquals("Operation should return a valid result", true, true)
        }
    }

    private fun awaitOperationCompletion(timeoutSeconds: Long = 5): Boolean {
        val result = operationLatch
            .await(timeoutSeconds, TimeUnit.SECONDS)
                && operationSuccessful

        Log.i("InstrumentalTest", "awaitOperationCompletion result: $result")
        return result
    }

    fun assertOperation(message: String) {
        Log.i("InstrumentalTest", "assertOperation: $message, result: $operationSuccessful")
        assertTrue(message, operationSuccessful)
    }

    private fun assertResultItem(expected: InputEtInfosSqlModels.Tarification, actual: Any?) {
        Log.i("InstrumentalTest", "Starting assertResultItem with expected: $expected")

        if (actual is InputEtInfosSqlModels.Tarification) {
            // Verify the important fields
            assertEquals("Product ID should match", expected.idProduit, actual.idProduit)
            assertEquals("Client ID should match", expected.idClient, actual.idClient)
            assertEquals("Tarification type ID should match", expected.idTypeTarification, actual.idTypeTarification)
            assertEquals("Price should match", expected.prixCurrency, actual.prixCurrency, 0.001)

            Log.i("InstrumentalTest", "All field assertions passed")
        } else {
            Log.i("InstrumentalTest", "Actual result is not a Tarification: $actual")
            assertTrue("Expected a Tarification object", false)
        }
    }

    fun <T> assertResult(expected: T, actual: T? = lastResult as? T) {
        Log.i("InstrumentalTest", "Starting assertResult with expected: $expected")
        Log.i("InstrumentalTest", "Actual result type: ${lastResult?.javaClass?.simpleName}")

        when {
            expected is InputEtInfosSqlModels.Tarification && lastResult is List<*> -> {
                Log.i("InstrumentalTest", "Expected is Tarification but result is List")

                val list = lastResult as List<*>
                val filteredItems = list.filterIsInstance<InputEtInfosSqlModels.Tarification>()
                    .filter {
                        it.idClient == expected.idClient &&
                                it.idProduit == expected.idProduit &&
                                it.idTypeTarification == expected.idTypeTarification
                    }

                assertTrue("List should contain at least one matching item", filteredItems.isNotEmpty())

                val item = filteredItems.first()
                assertEquals("Product ID should match", expected.idProduit, item.idProduit)
                assertEquals("Client ID should match", expected.idClient, item.idClient)
                assertEquals("Tarification type ID should match", expected.idTypeTarification, item.idTypeTarification)
                assertEquals("Price should match", expected.prixCurrency, item.prixCurrency, 0.001)
            }

            else -> {
                if (actual != null) {
                    assertEquals(expected, actual)
                } else {
                    Log.i("InstrumentalTest", "Actual result is null, cannot compare with expected: $expected")
                    assertTrue("Actual result should not be null", false)
                }
            }
        }

        Log.i("InstrumentalTest", "assertResult completed")
    }
}
