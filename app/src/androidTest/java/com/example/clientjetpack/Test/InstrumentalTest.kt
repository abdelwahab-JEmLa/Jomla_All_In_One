package com.example.clientjetpack.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorys
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
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
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.test.DefaultAsserter.fail

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class InstrumentalTest : KoinTest, TarificationViewModel.TestCallbacks {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .build()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TarificationViewModel
    private lateinit var mockRepository: InputEtInfosSqlGroupeRepositorys.TarificationRepository

    private val operationLatch = CountDownLatch(1)
    private var operationSuccessful = false
    private var lastResult: Any? = null

    override fun <T> onOperationSuccess(result: T) {
        lastResult = result
        operationSuccessful = true
        operationLatch.countDown()
        Log.i("onOperationSuccess(result: T)", "$lastResult")
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stopKoin()

        // Create a mock repository that we control
        mockRepository = Mockito.mock(InputEtInfosSqlGroupeRepositorys.TarificationRepository::class.java)

        startKoin {
            modules(
                module {
                    single { this@InstrumentalTest }
                    single { mockRepository }
                    single { TarificationViewModel(get()) }
                }
            )
        }

        viewModel = TarificationViewModel(this@InstrumentalTest)
        operationSuccessful = false
        lastResult = null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testLogWithMethodFilter() = runTest {
        // Reset lastResult avant le test pour garantir un état initial connu
        lastResult = null

        // Création des données de test
        val testData = InputEtInfosSqlModels.Tarification(
            vidTimestamp = (System.currentTimeMillis() - 86400000),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 1L,
            prixCurrency = 2.99
        )

        when (val result = lastResult) {
            is List<*> -> {
                if (result.isNotEmpty()) {
                    assertEquals(testData, result.first())
                } else {
                    fail("La liste lastResult est vide")
                }
            }
            else -> {
                assertEquals(testData, result)
            }
        }
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

}
