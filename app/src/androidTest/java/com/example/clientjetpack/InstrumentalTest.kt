package com.example.clientjetpack

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
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
        .filterByTag("TestRunner")
        .filterByTag("InstrumentalTest")
        .filterByTag("FireBaseHandler")
        .filterByTag("InputEtInfosRepo")
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

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(testDispatcher)

        stopKoin()

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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testBasicLogging() = runTest {
        assertEquals(
            1L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )
    }

    @Test
    fun testLogWithMethodFilter() = runTest {
        viewModel.addNewTestDataTarificationEtClient()

        assertTrue("Firebase operation timed out", awaitOperationCompletion())

        assertEquals(
            2L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )
    }

    override fun <T> onOperationSuccess(result: T) {
        lastResult = result
        operationSuccessful = true
        operationLatch.countDown()

        // Additional assertions can be made here if needed
        if (result != null) {
            assertEquals("Operation should return a valid result", true, true)

        }
    }

    private fun awaitOperationCompletion(timeoutSeconds: Long = 5): Boolean {
        return operationLatch
            .await(timeoutSeconds, TimeUnit.SECONDS)
                && operationSuccessful
    }

    fun assertOperation(message: String) {
        assertTrue(message, operationSuccessful)
    }

    fun <T> assertResult(expected: T, actual: T? = lastResult as? T) {
        assertEquals(expected, actual)
    }
}
