package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.InputSqlGroupeRepositorysImp
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.InputSqlGroupeRepositorysImp.Companion.clientRepository
import com.example.clientjetpack.Id1.PrixChangable.Test.Log.logProduits
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel.OutputViewModelNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel.TarificationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var b_GroupeRepositoryImp: InputSqlGroupeRepositorysImp
    private lateinit var tarificationRepo: InputSqlGroupeRepositorysImp.TarificationDataBaseFacileEntreRepositoryImp

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tarificationRepo =
            InputSqlGroupeRepositorysImp.TarificationDataBaseFacileEntreRepositoryImp()
        b_GroupeRepositoryImp = InputSqlGroupeRepositorysImp()
        viewModel = TarificationViewModel(tarificationRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun A_logSepareReferentialDataBases(): Unit = runTest {
        val initialClient = clientRepository.modelList.find { it.id == 1L }
        assertEquals(1L, initialClient?.idActiveTypeTarificationDataBase)

        SepareReferentialDataBases()
    }

    @Test
    fun B_logUpdateReferentialDataBases(): Unit = runTest {
        val newTarification = InputSqlModels.A_TarificationDataBaseFacileEntre(
            vidTimestamp = createTimestamp(day = 10, hour = 16, minute = 30),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 9.99
        )

        tarificationRepo.add(newTarification) { addedTarification ->
            val client = clientRepository.modelList.find { clientToUpdate ->
                clientToUpdate.id == addedTarification.idClient
            }?.copy(
                idActiveTypeTarificationDataBase = addedTarification.idTypeTarification
            )
            if (client != null) {
                clientRepository.update(client)
            }
        }

        val updatedClient = clientRepository.modelList.find { it.id == 1L }
        assertEquals(2L, updatedClient?.idActiveTypeTarificationDataBase)

        val name = "A_DataBasesSepareReferential_AfterUpdate"
        val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())
        println("\n========Apre Update========\n")
        println(
            "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                    "${currentStrTime.second} du  $name  ========"
        )

        testDispatcher.scheduler.advanceUntilIdle()
        val currentValue = viewModel.imbriquantFlow.value
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

            val currentValue = viewModel.imbriquantFlow.value
            mainLog(currentValue)

            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mainLog(value: OutputViewModelNoSqlModel) {
        println("\n-- Hierarchical Structure --")
        logProduits(value)
    }
}
