package Y_AppsFather.Kotlin.ViewModel

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel
import Y_AppsFather.Kotlin.ViewModel.Extensions.BonType
import Y_AppsFather.Z_AppsFather.Kotlin._3.Init.LoadFromFirebaseHandler.parseProduct
import Y_AppsFather.Z_AppsFather.Kotlin._3.Init.calculateurOktapuluse
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import com.example.Z_AppsFather.Kotlin._3.Init.CreeNewStart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    var _produitsAvecBonsGrossist = mutableStateListOf<ProduitModel>()
    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    private val _productFlow = MutableStateFlow<Map<Long, ProduitModel>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    val _bonCommandeFlow = MutableStateFlow<ProduitModel.GrossistBonCommandes?>(null)
    val _bonVentFlow = MutableStateFlow<ProduitModel.ClientBonVentModel?>(null)
    val bonCommandeFlow = _bonCommandeFlow.asStateFlow()
    val _bonTypeFlow = MutableStateFlow<BonType<*>?>(null)
    val bonTypeFlow = _bonTypeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                if (0 == 0) calculateurOktapuluse(this@ViewModelInitApp)
                else CreeNewStart(_modelAppsFather, 0)
                setupDataListeners()

                _ModelAppsFather.collectBonType(this@ViewModelInitApp, viewModelScope)

                isLoading = true
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProduitsAvecBonsGrossist() {
        _produitsAvecBonsGrossist.clear()
        _produitsAvecBonsGrossist
            .addAll(_modelAppsFather.produitsMainDataBase
                .filter { it.bonCommendDeCetteCota != null }
                .sortedBy {
                    it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                }
            )
    }

    private fun setupDataListeners() {
        _modelAppsFather.produitsMainDataBase.forEach { produit ->
            Log.d("SetupListener", "Setting up listener for product ${produit.id}")
            produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                val updatedProduct = parseProduct(snapshot)
                                if (updatedProduct != null) {
                                    val index =
                                        _modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == updatedProduct.id }
                                    if (index != -1) {
                                        _modelAppsFather.produitsMainDataBase[index] =
                                            updatedProduct
                                        updateProduitsAvecBonsGrossist()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("SetupListener", "Error updating product", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SetupListener", "Database error: ${error.message}")
                    }
                })
        }
    }
}
