package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Data class for representing UI state
data class PeriodesUiState(
    val periodesVent: SnapshotStateList<PeriodesVent> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class PeriodesViewModel(
    val appDatabase: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeriodesUiState())
    open val uiState: StateFlow<PeriodesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // This is fine now because getCount() is suspend
            val count = appDatabase.vendeursActiveDonsCettePeriodeDao().getCount()
            if (count == 0) {
                insertTestData()
            }
        }
        collecteConvertSQlToNoSqlDataBase()
    }

    private fun insertTestData() {
        viewModelScope.launch {
            try {
                // Create test data for vendeurs
                val vendeur1 = VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(Vendeur Test 1)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 1,
                    nom = "Vendeur Test 1",
                    quantity = 8
                )

                val vendeur2 = VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "2->(Vendeur Test 2)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 2,
                    nom = "Vendeur Test 2",
                    quantity = 2
                )

                // Create test data for produits
                val produit1 = ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(Produit Test 1)",
                    parentkeyID = "1->(Vendeur Test 1)",
                    startIndex = 1,
                    nom = "Produit Test 1",
                    quantity = 5
                )

                val produit2 = ProduitsVenduParLuiRoomSQlModel(
                    keyID = "2->(Produit Test 2)",
                    parentkeyID = "1->(Vendeur Test 1)",
                    startIndex = 2,
                    nom = "Produit Test 2",
                    quantity = 3
                )

                val produit3 = ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(Produit Test 3)",
                    parentkeyID = "2->(Vendeur Test 2)",
                    startIndex = 1,
                    nom = "Produit Test 3",
                    quantity = 2
                )

                // Insert test data
                appDatabase.vendeursActiveDonsCettePeriodeDao().insertAll(listOf(vendeur1, vendeur2))
                appDatabase.produitsVenduParLuiDao().insertAll(listOf(produit1, produit2, produit3))

                Log.d("PeriodesViewModel", "Test data inserted successfully")
            } catch (e: Exception) {
                Log.e("PeriodesViewModel", "Error inserting test data: ${e.message}", e)
            }
        }
    }

    private fun collecteConvertSQlToNoSqlDataBase() {
        viewModelScope.launch {
            try {
                // Combine flows from both DAOs to process data together
                val vendeursFlow = appDatabase.vendeursActiveDonsCettePeriodeDao().getAllAsFlow()
                val produitsFlow = appDatabase.produitsVenduParLuiDao().getAllAsFlow()

                combine(vendeursFlow, produitsFlow) { vendeursList, produitsList ->
                    Log.d("PeriodesViewModel", "Processing data: ${vendeursList.size} vendeurs, ${produitsList.size} produits")

                    // Create a periode map to group vendeurs by periode
                    val periodeMap = mutableMapOf<String, PeriodesVent>()

                    // First process vendeurs
                    vendeursList.forEach { vendeurModel ->
                        val periodeId = vendeurModel.parentkeyID
                        if (!periodeMap.containsKey(periodeId)) {
                            periodeMap[periodeId] = PeriodesVent().apply {
                                this.vendeursActiveDonsCettePeriode = mutableMapOf()
                            }
                        }

                        // Create vendeur object
                        val vendeur = VendeursActiveDonsCettePeriode().apply {
                            this.produitsVenduParLui = mutableMapOf()
                        }

                        (periodeMap[periodeId]!!.vendeursActiveDonsCettePeriode as MutableMap<String, VendeursActiveDonsCettePeriode>)[vendeurModel.keyID] = vendeur
                    }

                    // Then process produits
                    produitsList.forEach { produitModel ->
                        val vendeurId = produitModel.parentkeyID

                        // Find the vendeur in all periods
                        periodeMap.values.forEach { periode ->
                            periode.vendeursActiveDonsCettePeriode.forEach { (vendeurKey, vendeur) ->
                                if (vendeurKey == vendeurId) {
                                    // Create produit object
                                    val produit = ProduitsVenduParLui().apply {
                                        this.quantity = produitModel.quantity
                                    }

                                    // Add produit to vendeur
                                    (vendeur.produitsVenduParLui as MutableMap<String, ProduitsVenduParLui>)[produitModel.keyID] = produit

                                    Log.d("PeriodesViewModel", "Added product ${produitModel.keyID} to vendeur $vendeurKey with quantity ${produitModel.quantity}")
                                }
                            }
                        }
                    }

                    // Update UI state with collected data
                    _uiState.value = PeriodesUiState(
                        periodesVent = mutableStateListOf<PeriodesVent>().apply {
                            addAll(periodeMap.values)
                        }
                    )

                    Log.d("PeriodesViewModel", "UI state updated with ${periodeMap.size} periods")
                }.collect {}

            } catch (e: Exception) {
                // Handle errors
                Log.e("PeriodesViewModel", "Error collecting data: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
}
