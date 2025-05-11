package com.example.clientjetpack.ID1.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.A_TarificationTestData.initialTestData
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
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

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")
    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    private var result: List<InputEtInfosSqlModels.Tarification> = emptyList()

    @Before
    fun setup() = runTest {
        try {
            Dispatchers.setMain(testDispatcher)

            try {
                stopKoin()
            } catch (e: IllegalStateException) {
                Log.d("InstrumentalTest", "Koin was not started")
            }

            startKoin {
                modules(
                    module {
                        single { this@InstrumentalTestInterieur }
                    }
                )
            }

            // Clear database and wait for completion
            clearDatabaseAsync(sonDataBaseRef)

            // Add test data and wait for completion
            addAllToFireBaseAsync(initialTestData, sonDataBaseRef)

        } catch (e: Exception) {
            Log.e("InstrumentalTest", "Setup failed: ${e.message}", e)
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

        // Verify we have the correct number of items (check that all 3 test items were added)
        assertEquals(3, result.size)

        // Create expected test data (first item from initialTestData sorted by timestamp)
        val sortedTestData = initialTestData.sortedBy { it.vidTimestamp }
        val expectedData = sortedTestData.first()

        // Verify the first item matches our expected data
        val firstResult = result.first()
        assertEquals(expectedData.idProduit, firstResult.idProduit)
        assertEquals(expectedData.idClient, firstResult.idClient)
        assertEquals(expectedData.idTypeTarification, firstResult.idTypeTarification)
        assertEquals(expectedData.prixCurrency, firstResult.prixCurrency, 0.01)
    }

    private suspend fun clearDatabaseAsync(databaseRef: DatabaseReference) {
        return suspendCancellableCoroutine { continuation ->
            databaseRef.removeValue().addOnSuccessListener {
                Log.d("InstrumentalTest", "Database cleared successfully")
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                Log.e("InstrumentalTest", "Failed to clear database: ${exception.message}")
                continuation.resumeWithException(exception)
            }
        }
    }

    suspend fun <T> loadDatasAsync(databaseRef: DatabaseReference, dataClass: Class<T>): List<T> {
        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<T>()

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("InstrumentalTest", "Data loaded. Child count: ${snapshot.childrenCount}")
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(dataClass)?.let {
                            dataList.add(it)
                            Log.d("InstrumentalTest", "Added item: $it")
                        }
                    }

                    continuation.resume(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InstrumentalTest", "Firebase data load cancelled: ${error.message}")
                    continuation.resume(emptyList())
                }
            })
        }
    }

    suspend fun <T> addAllToFireBaseAsync(modelList: List<T>, databaseRef: DatabaseReference) {
        if (modelList.isEmpty()) return

        val tasks = modelList.map { item ->
            val key = when (item) {
                is InputEtInfosSqlModels.Tarification -> item.vidTimestamp.toString()
                is InputEtInfosSqlModels.ClientDataBase -> item.id.toString()
                is InputEtInfosSqlModels.ProduitInfos -> item.id.toString()
                is InputEtInfosSqlModels.TypeTarificationDataBase -> item.id.toString()
                else -> databaseRef.push().key
            } ?: databaseRef.push().key

            Log.d("InstrumentalTest", "Adding item with key $key: $item")

            suspendCancellableCoroutine<Unit> { continuation ->
                databaseRef.child(key!!).setValue(item).addOnSuccessListener {
                    Log.d("InstrumentalTest", "Successfully added item with key $key")
                    continuation.resume(Unit)
                }.addOnFailureListener { exception ->
                    Log.e("InstrumentalTest", "Failed to add item with key $key: ${exception.message}")
                    continuation.resumeWithException(exception)
                }
            }
        }

        // Wait for all tasks to complete
        tasks.forEach { it }

        Log.d("InstrumentalTest", "All items added to Firebase successfully")
    }
}
