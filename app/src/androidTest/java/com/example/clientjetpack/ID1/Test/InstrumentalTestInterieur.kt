package com.example.clientjetpack.ID1.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.A_TarificationTestData.initialTestData
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.LogFilterRule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.coroutines.resume

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

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")
    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    private var result: List<InputEtInfosSqlModels.Tarification> = emptyList()

    @Before
    fun setup() {
        try {
            Dispatchers.setMain(testDispatcher)

            try {
                stopKoin()
            } catch (e: IllegalStateException) {
                android.util.Log.d("InstrumentalTest", "Koin was not started")
            }

            startKoin {
                modules(
                    module {
                        single { this@InstrumentalTestInterieur }
                    }
                )
            }

            clearDatabase(sonDataBaseRef)

            addAllToFireBase(initialTestData, sonDataBaseRef)

            Thread.sleep(500)

        } catch (e: Exception) {
            android.util.Log.e("InstrumentalTest", "Setup failed: ${e.message}", e)
            throw e
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testLogWithMethodFilter() = runTest {
        // Load data from Firebase
        result = loadDatasAsync(sonDataBaseRef, InputEtInfosSqlModels.Tarification::class.java)
            .sortedBy { it.vidTimestamp }

        // Verify we have the correct number of items
        assertEquals(3, result.size)

        // Create expected test data (first item from initialTestData)
        val expectedData = initialTestData.first()

        // Verify the first item matches our expected data
        val firstResult = result.first()
        assertEquals(expectedData.idProduit, firstResult.idProduit)
        assertEquals(expectedData.idClient, firstResult.idClient)
        assertEquals(expectedData.idTypeTarification, firstResult.idTypeTarification)
        assertEquals(expectedData.prixCurrency, firstResult.prixCurrency, 0.01)
    }

    private fun clearDatabase(databaseRef: DatabaseReference) {
        databaseRef.removeValue()
    }

    suspend fun <T> loadDatasAsync(databaseRef: DatabaseReference, dataClass: Class<T>): List<T> {
        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<T>()

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(dataClass)?.let { dataList.add(it) }
                    }

                    continuation.resume(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(emptyList())
                }
            })
        }
    }

    fun <T> addAllToFireBase(modelList: List<T>, databaseRef: DatabaseReference) {
        if (modelList.isEmpty()) return

        modelList.forEach { item ->
            val key = when (item) {
                is InputEtInfosSqlModels.Tarification -> item.vidTimestamp.toString()
                is InputEtInfosSqlModels.ClientDataBase -> item.id.toString()
                is InputEtInfosSqlModels.ProduitInfos -> item.id.toString()
                is InputEtInfosSqlModels.TypeTarificationDataBase -> item.id.toString()
                else -> databaseRef.push().key
            }

            key?.let {
                databaseRef.child(it).setValue(item).addOnSuccessListener {
                    // Success handled silently
                }
            }
        }
    }
}
