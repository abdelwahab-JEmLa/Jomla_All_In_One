package com.example.clientjetpack
     /*
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Package_3.calQuantityButton
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class CalQuantityButtonInstrumentedTest {
    private lateinit var viewModelInitApp: ViewModelInitApp
    private lateinit var database: DatabaseReference

    @Before
    fun setup() {
        // Initialize Firebase
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.initializeApp(context)

        // Initialize database reference
        database = FirebaseDatabase.getInstance().reference
            .child("test_database")
            .child("produits")

        // Initialize ViewModel
        viewModelInitApp = ViewModelInitApp()

        // Clear test data
        runBlocking {
            database.removeValue().await()
        }
    }

    @Test
    fun testRandomQuantitiesWithFirebase() = runBlocking<Unit> {
        try {
            // Create and add test product
            val testProduct = _ModelAppsFather.ProduitModel(id = 1L)
            viewModelInitApp._modelAppsFather.produitsMainDataBase.add(testProduct)

            // Test with single random case for initial verification
            val randomQuantity = Random.nextInt(1, 100)
            val currentSale = SoldArticlesTabelle().apply {
                idArticle = 1L
            }

            val currentClient = ClientsModel().apply {
                idClientsSu = 1L
                nomClientsSu = "Test Client"
                couleurSu = "#FF0000"
            }

            val colorDetails = ColorsArticlesTabelle().apply {
                idColore = 1L
                nameColore = "Test Color"
                iconColore = "🎨"
            }

            // Execute function
            calQuantityButton(
                quantity = randomQuantity,
                currentSale = currentSale,
                currentClient = currentClient,
                colorDetails = colorDetails,
                viewModelInitApp = viewModelInitApp
            )

            // Wait for Firebase operation to complete
            kotlinx.coroutines.delay(2000) // Give time for Firebase update

            // Verify local state
            val updatedProduct = viewModelInitApp._modelAppsFather.produitsMainDataBase[0]
            assertNotNull("Product should exist in local database", updatedProduct)

            val bonVente = updatedProduct.bonsVentDeCetteCota.firstOrNull()
            assertNotNull("BonVente should exist", bonVente)

            val colorPurchase = bonVente.colours_Achete.firstOrNull()
            assertNotNull("Color purchase should exist", colorPurchase)
            assertEquals(randomQuantity, colorPurchase.quantity_Achete)

        } catch (e: Exception) {
            throw AssertionError("Test failed with exception: ${e.message}", e)
        }
    }

    @Test
    fun testSimpleQuantityUpdate() = runBlocking<Unit> {
        try {
            // Create and add test product
            val testProduct = _ModelAppsFather.ProduitModel(id = 1L)
            viewModelInitApp._modelAppsFather.produitsMainDataBase.add(testProduct)

            // Test with fixed values
            val currentSale = SoldArticlesTabelle().apply {
                idArticle = 1L
            }

            val currentClient = ClientsModel().apply {
                idClientsSu = 1L
                nomClientsSu = "Test Client"
                couleurSu = "#FF0000"
            }

            val colorDetails = ColorsArticlesTabelle().apply {
                idColore = 1L
                nameColore = "Test Color"
                iconColore = "🎨"
            }

            // Execute function with a simple quantity
            calQuantityButton(
                quantity = 5,
                currentSale = currentSale,
                currentClient = currentClient,
                colorDetails = colorDetails,
                viewModelInitApp = viewModelInitApp
            )

            // Wait for Firebase operation to complete
            kotlinx.coroutines.delay(2000)

            // Verify local state
            val updatedProduct = viewModelInitApp._modelAppsFather.produitsMainDataBase[0]
            assertNotNull("Product should exist", updatedProduct)

            val bonVente = updatedProduct.bonsVentDeCetteCota.firstOrNull()
            assertNotNull("BonVente should exist", bonVente)

            val colorPurchase = bonVente.colours_Achete.firstOrNull()
            assertNotNull("Color purchase should exist", colorPurchase)
            assertEquals(5, colorPurchase.quantity_Achete)

        } catch (e: Exception) {
            throw AssertionError("Test failed with exception: ${e.message}", e)
        }
    }

    @Test
    fun testNullSafety() = runBlocking<Unit> {
        try {
            // Test with null values to ensure proper handling
            val currentSale = null
            val currentClient = null
            val colorDetails = ColorsArticlesTabelle().apply {
                idColore = 1L
                nameColore = "Test Color"
                iconColore = "🎨"
            }

            // This should not throw an exception
            calQuantityButton(
                quantity = 5,
                currentSale = currentSale,
                currentClient = currentClient,
                colorDetails = colorDetails,
                viewModelInitApp = viewModelInitApp
            )

            // Verify that the state wasn't changed
            assertEquals(0, viewModelInitApp._modelAppsFather.produitsMainDataBase.size)

        } catch (e: Exception) {
            throw AssertionError("Test failed with exception: ${e.message}", e)
        }
    }
}       */
