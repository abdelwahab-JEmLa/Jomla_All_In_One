package com.example.clientjetpack.Id1.PrixChangable.Test.Main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.ID3.Test.testID2_Add
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialClientsData
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialProductsData
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.logHErartchiDataBase
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.mockOutputNoSqlModel
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

    // Change to mutable lists to support adding new elements
    private var tarificationEntries = mutableListOf<InputEtInfosSqlModels.Tarification>()
    private var produitInfos = mutableListOf<InputEtInfosSqlModels.ProduitInfos>()
    private var clientDataBase = mutableListOf<InputEtInfosSqlModels.ClientDataBase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Initialize with data from initialTestData
        tarificationEntries = initialTestData.toMutableList()
        produitInfos = initialProductsData.toMutableList()
        clientDataBase = initialClientsData.toMutableList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testID1_LogFrommock() = runTest {
        val testData = mockOutputNoSqlModel(
            tarificationEntries,
            produitInfos,
            clientDataBase
        )

        assertTrue(
            "Products list should not be empty",
            testData.produits.isNotEmpty()
        )

        logHErartchiDataBase(
            testData.produits.toMutableList(),
            "Frome mockOutputNoSqlModel()",
        )
    }

    @Test fun testID2_Add() = runTest { testID2_Add(tarificationEntries, produitInfos, clientDataBase) }

}
