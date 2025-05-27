package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.testDataproduitInfosList
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(
    var produitInfosList: SnapshotStateList<A_ProduitInfosTest> = mutableStateListOf(),
    val loadingProgress: Float = 0f,
    val error: String? = null,
)

class ViewModel_TestID2 : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ViewModel_TestID2"
    }

    init {
        Log.d(TAG, "ViewModel initialized")
        loadData()
    }

    private fun loadData() {
        Log.d(TAG, "Loading test data...")
        val testData = testDataproduitInfosList()
        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(testData)
        Log.d(TAG, "Loaded ${testData.size} products")

        // Log each product's initial actualiseSonImage value
        testData.forEach { product ->
            Log.d(TAG, "Product ${product.id} (${product.nom}) - Initial actualiseSonImage: ${product.actualiseSonImage}")
        }
    }

    fun updateActualisationImage(productId: Long? = null) {
        Log.d(TAG, "=== Starting updateActualisationImage ===")
        Log.d(TAG, "Target productId: $productId")

        val currentList = _uiState.value.produitInfosList
        Log.d(TAG, "Current list size: ${currentList.size}")

        val updatedList = if (productId != null) {
            Log.d(TAG, "Updating specific product: $productId")
            // Update specific product
            currentList.map { product ->
                if (product.id == productId) {
                    val oldValue = product.actualiseSonImage
                    val newValue = oldValue + 1
                    Log.d(TAG, "Product ${product.id} (${product.nom}): actualiseSonImage $oldValue -> $newValue")

                    product.copy(
                        actualiseSonImage = newValue,
                        timestamps = System.currentTimeMillis(),
                        needUpdate = true
                    )
                } else {
                    product
                }
            }
        } else {
            Log.d(TAG, "Updating ALL products")
            // Update all products
            currentList.map { product ->
                val oldValue = product.actualiseSonImage
                val newValue = oldValue + 1
                Log.d(TAG, "Product ${product.id} (${product.nom}): actualiseSonImage $oldValue -> $newValue")

                product.copy(
                    actualiseSonImage = newValue,
                    timestamps = System.currentTimeMillis(),
                    needUpdate = true
                )
            }
        }

        Log.d(TAG, "Clearing and updating product list...")
        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(updatedList)

        Log.d(TAG, "StateFlow emission completed")
        Log.d(TAG, "=== updateActualisationImage completed ===")

        // Log final state
        _uiState.value.produitInfosList.forEach { product ->
            Log.d(TAG, "Final state - Product ${product.id}: actualiseSonImage=${product.actualiseSonImage}, needUpdate=${product.needUpdate}")
        }
    }

    fun updateProduct(updatedProduct: A_ProduitInfosTest) {
        Log.d(TAG, "=== updateProduct called ===")
        Log.d(TAG, "Updating product: ${updatedProduct.id} (${updatedProduct.nom})")
        Log.d(TAG, "New actualiseSonImage: ${updatedProduct.actualiseSonImage}")

        val currentList = _uiState.value.produitInfosList
        val index = currentList.indexOfFirst { it.id == updatedProduct.id }

        if (index != -1) {
            val oldProduct = currentList[index]
            Log.d(TAG, "Found product at index $index")
            Log.d(TAG, "Old actualiseSonImage: ${oldProduct.actualiseSonImage}")

            currentList[index] = updatedProduct.copy(
                timestamps = System.currentTimeMillis(),
                needUpdate = true
            )

            Log.d(TAG, "Product updated successfully")
        } else {
            Log.w(TAG, "Product with ID ${updatedProduct.id} not found in list")
        }

        Log.d(TAG, "=== updateProduct completed ===")
    }

    fun addNewProduct(newProduct: A_ProduitInfosTest) {
        Log.d(TAG, "=== addNewProduct called ===")
        Log.d(TAG, "Adding new product: ${newProduct.id} (${newProduct.nom})")
        Log.d(TAG, "actualiseSonImage: ${newProduct.actualiseSonImage}")

        _uiState.value.produitInfosList.add(newProduct)

        Log.d(TAG, "New product added. Total products: ${_uiState.value.produitInfosList.size}")
        Log.d(TAG, "=== addNewProduct completed ===")
    }
}
