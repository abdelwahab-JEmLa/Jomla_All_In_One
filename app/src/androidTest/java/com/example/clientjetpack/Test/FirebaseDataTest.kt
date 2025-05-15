package com.example.clientjetpack.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.FireBaseHandler
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.D_TarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.TypeTarificationEnum
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.TestAppDatabase
import com.example.clientjetpack.ID1.Test.ID2.Test.LogFilterRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FirebaseDataTest : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("FirebaseDataTest")
        .build()

    private val testDispatcher = StandardTestDispatcher()
    private val repositoriesImpl: _InfosSqlDataBases_GroupeRepositorysImp by inject()
    private val fireBaseHandler: FireBaseHandler by inject()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                module {
                    single {
                        TestAppDatabase.getTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
                    }
                    single {
                        _InfosSqlDataBases_GroupeRepositorysImp(
                            InstrumentationRegistry.getInstrumentation().targetContext,
                            get()
                        )
                    }
                    single { FireBaseHandler() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testFirebaseWriteAndRead() = runTest {
        // 1. SETUP: Clean local databases and Firebase
        LogFilterRule.log("FirebaseDataTest", "Starting Firebase write and read test")
        cleanAllData()
        
        // 2. CREATE TEST DATA
        val produit = A_ProduitInfos(id = 100, nom = "Firebase Test Product")
        val client = B_ClientInfos(id = 100, nom = "Firebase Test Client", idActiveTypeTarificationDataBase = 1)
        val typeTarification = C_TypeTarificationInfos(id = 100, typeTarificationEnum = TypeTarificationEnum.ParBenifice)
        val tarification = D_TarificationInfos(
            vidTimestamp = System.currentTimeMillis(),
            idProduit = 100,
            idClient = 100,
            idTypeTarification = 100,
            prixCurrency = 50.0
        )

        // 3. WRITE DATA DIRECTLY TO FIREBASE
        LogFilterRule.log("FirebaseDataTest", "Writing data directly to Firebase")
        fireBaseHandler.addToFirebaseAsync(produit)
        fireBaseHandler.addToFirebaseAsync(client)
        fireBaseHandler.addToFirebaseAsync(typeTarification)
        fireBaseHandler.addToFirebaseAsync(tarification)
        
        // Give time for Firebase write operations to complete
        testDispatcher.scheduler.advanceTimeBy(5000) // 5 seconds
        
        // 4. ATTEMPT TO READ DATA FROM FIREBASE
        LogFilterRule.log("FirebaseDataTest", "Reading data from Firebase directly")
        var fbProduits: List<A_ProduitInfos> = emptyList()
        var fbClients: List<B_ClientInfos> = emptyList()
        var fbTypeTarifications: List<C_TypeTarificationInfos> = emptyList()
        var fbTarifications: List<D_TarificationInfos> = emptyList()
        
        try {
            fbProduits = fireBaseHandler.loadDatasAsync(
                fireBaseHandler.getProduitRef(),
                A_ProduitInfos::class.java
            )
            LogFilterRule.log("FirebaseDataTest", "Firebase produits loaded: ${fbProduits.size}")
            
            fbClients = fireBaseHandler.loadDatasAsync(
                fireBaseHandler.getClientRef(),
                B_ClientInfos::class.java
            )
            LogFilterRule.log("FirebaseDataTest", "Firebase clients loaded: ${fbClients.size}")
            
            fbTypeTarifications = fireBaseHandler.loadDatasAsync(
                fireBaseHandler.getTypeTarificationRef(),
                C_TypeTarificationInfos::class.java
            )
            LogFilterRule.log(
                "FirebaseDataTest",
                "Firebase type tarifications loaded: ${fbTypeTarifications.size}"
            )
            
            fbTarifications = fireBaseHandler.loadDatasAsync(
                fireBaseHandler.getTarificationRef(),
                D_TarificationInfos::class.java
            )
            LogFilterRule.log(
                "FirebaseDataTest",
                "Firebase tarifications loaded: ${fbTarifications.size}"
            )
        } catch (e: Exception) {
            LogFilterRule.log("FirebaseDataTest", "ERROR loading from Firebase: ${e.message}")
            e.printStackTrace()
        }
        
        // 5. VERIFY DIRECT FIREBASE ACCESS RESULTS
        LogFilterRule.log("FirebaseDataTest", "Verifying data from direct Firebase access")
        verifyFirebaseItem(fbProduits, produit, "produit")
        verifyFirebaseItem(fbClients, client, "client")
        verifyFirebaseItem(fbTypeTarifications, typeTarification, "type tarification")
        verifyFirebaseItem(fbTarifications, tarification, "tarification")
        
        // 6. LOAD DATA FROM FIREBASE TO LOCAL DATABASE
        LogFilterRule.log("FirebaseDataTest", "Loading data from Firebase to local database")
        try {
            (repositoriesImpl.produitRepository as _InfosSqlDataBases_GroupeRepositorysImp.A_ProduitInfos_RepositoryImpl)
                .loadDataFromFirebase()
            
            (repositoriesImpl.clientRepository as _InfosSqlDataBases_GroupeRepositorysImp.B_ClientInfos_RepositoryImpl)
                .loadDataFromFirebase()
            
            (repositoriesImpl.typeTarificationRepository as _InfosSqlDataBases_GroupeRepositorysImp.C_TypeTarificationInfos_RepositoryImpl)
                .loadDataFromFirebase()
            
            repositoriesImpl.tarificationRepository.loadDataFromFirebase()
        } catch (e: Exception) {
            LogFilterRule.log(
                "FirebaseDataTest",
                "ERROR loading from Firebase to local: ${e.message}"
            )
            e.printStackTrace()
        }
        
        // Give time for local database operations to complete
        testDispatcher.scheduler.advanceTimeBy(5000) // 5 seconds
        
        // 7. VERIFY LOCAL DATABASE AFTER LOAD FROM FIREBASE
        LogFilterRule.log("FirebaseDataTest", "Verifying local database after load from Firebase")
        val localProduits = repositoriesImpl.produitRepository.modelListFlow.first()
        val localClients = repositoriesImpl.clientRepository.modelListFlow.first()
        val localTarifications = repositoriesImpl.tarificationRepository.modelListFlow.first()

        LogFilterRule.log("FirebaseDataTest", "Local produits: ${localProduits.size}")
        LogFilterRule.log("FirebaseDataTest", "Local clients: ${localClients.size}")
        LogFilterRule.log("FirebaseDataTest", "Local tarifications: ${localTarifications.size}")
        
        // Verify the test items were loaded from Firebase to local database
        verifyLocalItem(localProduits, produit, "produit")
        verifyLocalItem(localClients, client, "client")
        verifyLocalItem(localTarifications, tarification, "tarification")
    }
    
    @Test
    fun testFirebaseConnectivity() = runTest {
        LogFilterRule.log("FirebaseDataTest", "Testing Firebase connectivity")
        
        try {
            val ref = fireBaseHandler.getProduitRef()
            suspendCoroutine<Unit> { continuation ->
                ref.child("connectivity_test").setValue("test_value")
                    .addOnSuccessListener {
                        LogFilterRule.log("FirebaseDataTest", "SUCCESS: Firebase write succeeded")
                        ref.child("connectivity_test").removeValue()
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        LogFilterRule.log(
                            "FirebaseDataTest",
                            "ERROR: Firebase write failed: ${e.message}"
                        )
                        continuation.resume(Unit)
                    }
            }
        } catch (e: Exception) {
            LogFilterRule.log(
                "FirebaseDataTest",
                "EXCEPTION during Firebase connectivity test: ${e.message}"
            )
            e.printStackTrace()
        }
    }
    
    @Test
    fun testFirebaseReferenceCorrectness() = runTest {
        LogFilterRule.log("FirebaseDataTest", "Testing Firebase reference paths")
        
        val produitRef = fireBaseHandler.getProduitRef()
        val clientRef = fireBaseHandler.getClientRef()
        val typeTarificationRef = fireBaseHandler.getTypeTarificationRef()
        val tarificationRef = fireBaseHandler.getTarificationRef()

        LogFilterRule.log("FirebaseDataTest", "Produit ref path: ${produitRef.path}")
        LogFilterRule.log("FirebaseDataTest", "Client ref path: ${clientRef.path}")
        LogFilterRule.log(
            "FirebaseDataTest",
            "Type tarification ref path: ${typeTarificationRef.path}"
        )
        LogFilterRule.log("FirebaseDataTest", "Tarification ref path: ${tarificationRef.path}")
        
        val produitRefByType = fireBaseHandler.getRefByType(A_ProduitInfos::class.java)
        LogFilterRule.log(
            "FirebaseDataTest",
            "Ref by type path equals direct ref: ${produitRefByType.path == produitRef.path}"
        )
    }
    
    private fun cleanAllData() = runTest {
        // Clean local databases
        repositoriesImpl.produitRepository.deleteAll()
        repositoriesImpl.clientRepository.deleteAll()
        repositoriesImpl.typeTarificationRepository.deleteAll()
        repositoriesImpl.tarificationRepository.deleteAll()
        
        // Clean Firebase databases
        try {
            fireBaseHandler.clearDatabaseAsync(fireBaseHandler.getProduitRef())
            fireBaseHandler.clearDatabaseAsync(fireBaseHandler.getClientRef())
            fireBaseHandler.clearDatabaseAsync(fireBaseHandler.getTypeTarificationRef())
            fireBaseHandler.clearDatabaseAsync(fireBaseHandler.getTarificationRef())
        } catch (e: Exception) {
            LogFilterRule.log("FirebaseDataTest", "Error clearing Firebase: ${e.message}")
            e.printStackTrace()
        }
        
        // Allow time for operations to complete
        testDispatcher.scheduler.advanceTimeBy(3000)
    }
    
    private fun <T> verifyFirebaseItem(items: List<T>, expectedItem: T, itemType: String) {
        if (items.isEmpty()) {
            LogFilterRule.log("FirebaseDataTest", "ERROR: No $itemType items found in Firebase")
        } else {
            val containsItem = items.any { it.toString() == expectedItem.toString() }
            if (containsItem) {
                LogFilterRule.log("FirebaseDataTest", "SUCCESS: $itemType found in Firebase")
            } else {
                LogFilterRule.log("FirebaseDataTest", "ERROR: $itemType not found in Firebase")
                LogFilterRule.log("FirebaseDataTest", "Expected: $expectedItem")
                LogFilterRule.log("FirebaseDataTest", "Available items: $items")
            }
        }
    }
    
    private fun <T> verifyLocalItem(items: List<T>, expectedItem: T, itemType: String) {
        if (items.isEmpty()) {
            LogFilterRule.log(
                "FirebaseDataTest",
                "ERROR: No $itemType items found in local database"
            )
        } else {
            val containsItem = items.any { it.toString() == expectedItem.toString() }
            if (containsItem) {
                LogFilterRule.log("FirebaseDataTest", "SUCCESS: $itemType found in local database")
            } else {
                LogFilterRule.log(
                    "FirebaseDataTest",
                    "ERROR: $itemType not found in local database"
                )
                LogFilterRule.log("FirebaseDataTest", "Expected: $expectedItem")
                LogFilterRule.log("FirebaseDataTest", "Available items: $items")
            }
        }
    }
}
