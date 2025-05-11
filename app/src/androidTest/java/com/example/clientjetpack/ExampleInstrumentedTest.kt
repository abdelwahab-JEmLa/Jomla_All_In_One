package com.example.clientjetpack

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InstrumentedTest : KoinComponent {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val logFilterRule = EnhancedLogFilterRule(
        includeOnly = listOf("TestRunner", "System.out"),
        excludeTags = listOf(
            "ConfigStore", "ANDR-PERF-MPCTL", "chatty", "NetworkSession",
            "vendor.qti.hardware", "ogle.android.g"
        )
    )

    private val testDispatcher = StandardTestDispatcher()

    // Inject the viewModel using Koin instead of directly instantiating it
    private val viewModel: TarificationViewModel by inject()

    @Before
    fun setup() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        println("Testing on ${appContext.packageName}")

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun A_logSepareReferentialDataBases(): Unit = runTest {
        assertEquals(
            1L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )
    }

    @Test
    fun B_logUpdateReferentialDataBases(): Unit = runTest {
        viewModel.addNewTestDataTarificationEtClient()

        assertEquals(
            2L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )
    }
}
