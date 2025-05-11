package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.Log.logProduits
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel.TarificationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    lateinit var viewModel: TarificationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = TarificationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun A_logSepareReferentialDataBases(): Unit = runTest {

        assertEquals(
            1L,
            viewModel.getClient(1)?.idActiveTypeTarificationDataBase
        )

        SepareReferentialDataBases()
    }

    @Test
    fun B_logUpdateReferentialDataBases(): Unit = runTest {

       viewModel.addNewTestDataTarificationEtClient()

        assertEquals(
            2L,
            viewModel.getClient(1)?.idActiveTypeTarificationDataBase
        )
        val name = "A_DataBasesSepareReferential_AfterUpdate"
        val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())
        println("\n========Apre Update========\n")
        println(
            "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                    "${currentStrTime.second} du  $name  ========"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val currentValue = viewModel.outputNoSqlFlow.first()

        mainLog(currentValue)

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")
    }


    private fun SepareReferentialDataBases() = runTest {
        try {
            val name = "A_DataBasesSepareReferential"
            val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())
            println(
                "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                        "${currentStrTime.second} du  $name  ========"
            )

            testDispatcher.scheduler.advanceUntilIdle()
            val currentValue = viewModel.outputNoSqlFlow.first()

            mainLog(currentValue)

            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mainLog(value: OutputNoSqlModel) {
        println("\n-- Hierarchical Structure --")
        logProduits(value)
    }
}
