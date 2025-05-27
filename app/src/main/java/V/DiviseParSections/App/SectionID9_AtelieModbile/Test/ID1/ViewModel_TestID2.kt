package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

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

class ViewModel_TestID2(
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData(): Unit {
        // Load test data with products that have prixVent > 0
        val testData = testDataproduitInfosList()
        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(testData)
    }

    fun addProduct(product: A_ProduitInfosTest) {
        _uiState.value.produitInfosList.add(product)
    }

    fun removeProduct(productId: Long) {
        _uiState.value.produitInfosList.removeAll { it.id == productId }
    }

    fun updateProduct(product: A_ProduitInfosTest) {
        val index = _uiState.value.produitInfosList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            _uiState.value.produitInfosList[index] = product
        }
    }
}
