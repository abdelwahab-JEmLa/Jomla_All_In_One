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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ExampleLogFilterTest : KoinComponent {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    // Combined rule that captures all necessary log filtering functionality
    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("TestRunner")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    private val viewModel: TarificationViewModel by inject()

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

        assertEquals(
            2L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )
    }
}
