package com.example.clientjetpack

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InstrumentalTest : KoinTest, TarificationViewModel.TestCallbacks {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess(result: T)")
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

    override fun <T> onOperationSuccess(result: T) {
        lastResult = result
        operationSuccessful = true
        operationLatch.countDown()
        Log.i("onOperationSuccess(result: T)", "$lastResult")
    }

    @Before
    fun setup() {
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
    fun testLogWithMethodFilter() = runTest {
        val testData = InputEtInfosSqlModels.Tarification(
            vidTimestamp = (System.currentTimeMillis() - 86400000),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 1L,
            prixCurrency = 2.99
        )
        if (lastResult is List<*> && (lastResult as List<*>).isNotEmpty()) {
            assertEquals((lastResult as List<*>).first(), testData)
        } else {
            assertEquals(lastResult, testData)
        }

    }
}
