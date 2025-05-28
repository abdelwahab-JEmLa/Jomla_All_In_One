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

    init {
        loadData()
    }

    private fun loadData() {
        val testData = testDataproduitInfosList()
        _uiState.value.produitInfosList.clear()
        _uiState.value.produitInfosList.addAll(testData)
    }


    fun updateActualisationImage(productId: Long? = null) {
        val currentList = _uiState.value.produitInfosList

        if (productId != null) {
            val index = currentList.indexOfFirst { it.id == productId }
            if (index != -1) {
                val product = currentList[index]
                val newValue = product.actualiseSonImage + 1
                val newTestValue = product.actualiseSonImageTest2 + 1

                currentList[index] = product.copy(
                    actualiseSonImage = newValue,
                    actualiseSonImageTest2 = newTestValue, // Mettre à jour les deux compteurs
                    timestamps = System.currentTimeMillis(),
                    needUpdate = true
                )

                // Forcer la mise à jour de l'état pour déclencher la recomposition
                _uiState.value = _uiState.value.copy(
                    produitInfosList = currentList
                )

                Log.d("ViewModel_TestID2", "Updated product $productId: refresh=${newValue}, test2=${newTestValue}")
            }
        }
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
