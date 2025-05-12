package com.example.clientjetpack.ID1.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID1.Test.A_TarificationTestData.initialTestData
import com.example.clientjetpack.LogFilterRule
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InstrumentalTestInterieur : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .build()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fireBaseHandler: FireBaseHandler // Changed to lateinit var

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")
    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    private var result: List<InputEtInfosSqlModels.Tarification> = emptyList()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)

        fireBaseHandler = FireBaseHandler()

        clearDatabaseAsync(sonDataBaseRef)
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testLog() = runTest {
        // Fixed the unresolved reference by explicitly defining the lambda parameter
        result = fireBaseHandler.loadDatasAsync(sonDataBaseRef, InputEtInfosSqlModels.Tarification::class.java)
            .sortedBy { tarification -> tarification.vidTimestamp }

        assertEquals(3, result.size)

        val sortedTestData = initialTestData.sortedBy { it.vidTimestamp }
        val expectedData = sortedTestData.first()

        val firstResult = result.first()
        assertEquals(expectedData.idProduit, firstResult.idProduit)
        assertEquals(expectedData.idClient, firstResult.idClient)
        assertEquals(expectedData.idTypeTarification, firstResult.idTypeTarification)
        assertEquals(expectedData.prixCurrency, firstResult.prixCurrency, 0.01)
    }

    private suspend fun clearDatabaseAsync(databaseRef: DatabaseReference) {
        return suspendCancellableCoroutine { continuation ->
            databaseRef.removeValue().addOnSuccessListener {
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }
}

