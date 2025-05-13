package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialClientsData
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialProductsData
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.initialTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.InputEtInfosSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.testID1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class __TestsDisplayerLogDataBase {
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

    @Test fun testID2_AddLogFrommock() = runTest { testID1(tarificationEntries, produitInfos, clientDataBase) }
}
