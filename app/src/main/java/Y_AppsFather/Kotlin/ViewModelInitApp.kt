package Y_AppsFather.Kotlin

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.Companion.observeProductUpdates
import Y_AppsFather.Z_AppsFather.Kotlin._3.Init.calculateurOktapuluse
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import com.example.Z_AppsFather.Kotlin._3.Init.CreeNewStart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())

    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var _produitsAvecBonsGrossist = mutableStateListOf<ProduitModel>()
    val produitsAvecBonsGrossist: List<ProduitModel> get() = _produitsAvecBonsGrossist

    // Add this getter for when we need the mutable list
    val produitsAvecBonsGrossistMutable: SnapshotStateList<ProduitModel> get() = _produitsAvecBonsGrossist
    private val _productFlow = MutableStateFlow<Map<Long, ProduitModel>>(emptyMap())
    val productFlow: StateFlow<Map<Long, ProduitModel>> = _productFlow.asStateFlow()

    fun updateProduitsAvecBonsGrossist() {
        _produitsAvecBonsGrossist.clear()
        _produitsAvecBonsGrossist
            .addAll(produitsMainDataBase
                .filter { it.bonCommendDeCetteCota != null }
                .sortedBy {
                    it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                }
            )
    }

    var initializationComplete by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    init {
        viewModelScope.launch {
            try {
                this@ViewModelInitApp.isLoading = true

                val NOMBRE_ENTRE = 0

                if (NOMBRE_ENTRE == 0) {
                    calculateurOktapuluse(this@ViewModelInitApp)
                } else {
                    CreeNewStart(
                        _modelAppsFather,
                        NOMBRE_ENTRE,
                    )
                }
                updateProduitsAvecBonsGrossist()
                setupDataListeners()
                // In ViewModelInitApp.kt, replace the problematic line with:
                observeProductUpdates(viewModelScope, this@ViewModelInitApp)
                initializationComplete = true
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Initialization failed", e)
                this@ViewModelInitApp.loadingProgress = 0f
                initializationComplete = false
            } finally {
                this@ViewModelInitApp.isLoading = false
            }
        }
    }

    private fun setupDataListeners() {
        viewModelScope.launch {
            _modelAppsFather.produitsMainDataBase.forEach { produit ->
                produitsFireBaseRef.child(produit.id.toString())
                    .child("bonsVentDeCettaCota")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            viewModelScope.launch {
                                try {
                                    val updatedBonsVent = snapshot.children.mapNotNull { child ->
                                        child.getValue(ProduitModel.ClientBonVentModel::class.java)
                                    }

                                    val index =
                                        _modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == produit.id }
                                    if (index != -1) {
                                        val updatedProduct =
                                            _modelAppsFather.produitsMainDataBase[index].apply {
                                                bonsVentDeCetteCota.clear()
                                                bonsVentDeCetteCota.addAll(updatedBonsVent)
                                            }

                                        // Emit the single updated product
                                        _productFlow.value =
                                            mapOf(updatedProduct.id to updatedProduct)

                                        updateProduitsAvecBonsGrossist()
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "ViewModelInitApp",
                                        "Error updating bonsVentDeCetteCota for product ${produit.id}",
                                        e
                                    )
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(
                                "ViewModelInitApp",
                                "Firebase bonsVentDeCetteCota listener cancelled for product ${produit.id}",
                                error.toException()
                            )
                        }
                    })
            }
        }
    }

    // Clean up when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            _productFlow.value = emptyMap()
        }
        produitsFireBaseRef.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
