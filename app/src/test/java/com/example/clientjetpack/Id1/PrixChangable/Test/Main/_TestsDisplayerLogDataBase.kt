package com.example.clientjetpack.Id1.PrixChangable.Test.Main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.InputEtInfosSqlModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Fix: Initialize as empty list with proper type
    private var tarificationEntries = emptyList<InputEtInfosSqlModels.Tarification>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tarificationEntries = initialTestData

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testID1_LogFrommock() = runTest {
        val testData = mockOutputNoSqlModel(tarificationEntries)

        assertTrue(
            "Products list should not be empty",
            testData.produits.isNotEmpty()
        )

        log(
            testData.produits.toMutableList(),
            "Frome mockOutputNoSqlModel()",
        )
    }

    fun testID2_() = runTest {
        val testData = mockOutputNoSqlModel(tarificationEntries)

        log(
            testData.produits.toMutableList(),
            "Frome mockOutputNoSqlModel()",
        )
    }

    fun addDataLog(): Unit {

    }

}
