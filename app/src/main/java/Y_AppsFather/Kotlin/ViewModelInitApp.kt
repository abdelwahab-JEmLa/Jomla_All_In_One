package Y_AppsFather.Kotlin

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel
import Y_AppsFather.Z_AppsFather.Kotlin._3.Init.calculateurOktapuluse
import android.util.Log
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    var _produitsAvecBonsGrossist = mutableStateListOf<ProduitModel>()

    private val _productFlow = MutableStateFlow<Map<Long, ProduitModel>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                if (0 == 0) calculateurOktapuluse(this@ViewModelInitApp)
                else CreeNewStart(_modelAppsFather, 0)
                setupSimpleDataListener()
                initializationComplete = true
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun setupSimpleDataListener() {
        _modelAppsFather.produitsMainDataBase.forEach { produit ->
            produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                // Update bons de vente
                                snapshot.child("bonsVentDeCetteCota").children
                                    .mapNotNull { it.getValue(ProduitModel.ClientBonVentModel::class.java) }
                                    .let { newBonsVent ->
                                        val index = _modelAppsFather.produitsMainDataBase
                                            .indexOfFirst { it.id == produit.id }

                                        if (index != -1) {
                                            _modelAppsFather.produitsMainDataBase[index].apply {
                                                bonsVentDeCetteCota.clear()
                                                bonsVentDeCetteCota.addAll(newBonsVent)
                                            }
                                        }
                                    }

                                // Update bon de commande
                                snapshot.child("bonCommendDeCetteCota")
                                    .getValue(ProduitModel.GrossistBonCommandes::class.java)
                                    ?.let { newBonCommande ->
                                        val index = _modelAppsFather.produitsMainDataBase
                                            .indexOfFirst { it.id == produit.id }

                                        if (index != -1) {
                                            _modelAppsFather.produitsMainDataBase[index]
                                                .bonCommendDeCetteCota = newBonCommande
                                        }
                                    }

                                // Update product flow and grossist list
                                val updatedProduct = _modelAppsFather.produitsMainDataBase
                                    .find { it.id == produit.id }

                                updatedProduct?.let {
                                    _productFlow.value = mapOf(it.id to it)
                                    updateProduitsAvecBonsGrossist()
                                }

                            } catch (e: Exception) {
                                Log.e("ViewModelInitApp", "Update failed for ${produit.id}", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ViewModelInitApp", "Firebase error: ${error.message}")
                    }
                })
        }
    }

    private fun updateProduitsAvecBonsGrossist() {
        _produitsAvecBonsGrossist.clear()
        _produitsAvecBonsGrossist.addAll(
            _modelAppsFather.produitsMainDataBase
                .filter { it.bonCommendDeCetteCota != null }
                .sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            _productFlow.value = emptyMap()
        }
    }
}
