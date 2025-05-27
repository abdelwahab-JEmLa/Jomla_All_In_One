package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.testDataproduitInfosList
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

    init {
        loadData()
    }

    private fun loadData() {
        val testData = testDataproduitInfosList()
        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(testData)
    }

    // FIXED TODO(1): Implement updateActualisationImage
    fun updateActualisationImage(productId: Long? = null) {
        val currentList = _uiState.value.produitInfosList
        val updatedList = if (productId != null) {
            // Update specific product
            currentList.map { product ->
                if (product.id == productId) {
                    product.copy(
                        actualiseSonImage = product.actualiseSonImage + 1,
                        timestamps = System.currentTimeMillis(),
                        needUpdate = true
                    )
                } else {
                    product
                }
            }
        } else {
            // Update all products
            currentList.map { product ->
                product.copy(
                    actualiseSonImage = product.actualiseSonImage + 1,
                    timestamps = System.currentTimeMillis(),
                    needUpdate = true
                )
            }
        }

        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(updatedList)
    }

    fun updateProduct(updatedProduct: A_ProduitInfosTest) {
        val currentList = _uiState.value.produitInfosList
        val index = currentList.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            currentList[index] = updatedProduct.copy(
                timestamps = System.currentTimeMillis(),
                needUpdate = true
            )
        }
    }

    fun addNewProduct(newProduct: A_ProduitInfosTest) {
        _uiState.value.produitInfosList.add(newProduct)
    }
}
