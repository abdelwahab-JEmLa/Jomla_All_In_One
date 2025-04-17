package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao.ProduitsVenduParLui_RoomSQlModelDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao.VendeursActiveDonsCettePeriode_RoomSQlModelDao
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Data class for representing UI state
data class PeriodesUiState(
    val periodesVent: SnapshotStateList<PeriodesVent> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class PeriodesViewModel(
    private val vendeursDao: VendeursActiveDonsCettePeriode_RoomSQlModelDao,
    private val produitsDao: ProduitsVenduParLui_RoomSQlModelDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeriodesUiState())
    open val uiState: StateFlow<PeriodesUiState> = _uiState.asStateFlow()

    init {
        insertTestData()
        collecteConvertSQlToNoSqlDataBase()
    }

    private fun insertTestData() {
        viewModelScope.launch {
            // Create test data for vendeurs
            val vendeur1 = VendeursActiveDonsCettePeriode.RoomSQlModel(
                keyID = "1->(Vendeur Test 1)",
                parentkeyID = "2023_04_17->(14:30)",
                startIndex = 1,
                nom = "Vendeur Test 1",
                quantity = 8
            )

            val vendeur2 = VendeursActiveDonsCettePeriode.RoomSQlModel(
                keyID = "2->(Vendeur Test 2)",
                parentkeyID = "2023_04_17->(14:30)",
                startIndex = 2,
                nom = "Vendeur Test 2",
                quantity = 2
            )

            // Create test data for produits
            val produit1 = ProduitsVenduParLui.RoomSQlModel(
                keyID = "1->(Produit Test 1)",
                parentkeyID = "1->(Vendeur Test 1)",
                startIndex = 1,
                nom = "Produit Test 1",
                quantity = 5
            )

            val produit2 = ProduitsVenduParLui.RoomSQlModel(
                keyID = "2->(Produit Test 2)",
                parentkeyID = "1->(Vendeur Test 1)",
                startIndex = 2,
                nom = "Produit Test 2",
                quantity = 3
            )

            val produit3 = ProduitsVenduParLui.RoomSQlModel(
                keyID = "1->(Produit Test 3)",
                parentkeyID = "2->(Vendeur Test 2)",
                startIndex = 1,
                nom = "Produit Test 3",
                quantity = 2
            )

            // Insert test data
            vendeursDao.insertAll(listOf(vendeur1, vendeur2))
            produitsDao.insertAll(listOf(produit1, produit2, produit3))
        }
    }

    private fun collecteConvertSQlToNoSqlDataBase() {
        viewModelScope.launch {
            try {
                // Create a periode map to group vendeurs by periode
                val periodeMap = mutableMapOf<String, PeriodesVent>()

                // Collect vendeurs
                vendeursDao.getAllAsFlow().collectLatest { vendeursList ->
                    val vendeursMap = mutableMapOf<String, MutableMap<String, VendeursActiveDonsCettePeriode>>()

                    // Group vendeurs by periode
                    vendeursList.forEach { vendeurModel ->
                        val periodeId = vendeurModel.parentkeyID
                        if (!vendeursMap.containsKey(periodeId)) {
                            vendeursMap[periodeId] = mutableMapOf()
                        }

                        // Create vendeur object
                        val vendeur = VendeursActiveDonsCettePeriode().apply {
                            this.produitsVenduParLui = mutableMapOf()
                        }

                        vendeursMap[periodeId]!![vendeurModel.keyID] = vendeur
                    }

                    // Create periodes from vendeurs map
                    vendeursMap.forEach { (periodeId, vendeurs) ->
                        val periode = PeriodesVent().apply {
                            this.vendeursActiveDonsCettePeriode = vendeurs
                        }
                        periodeMap[periodeId] = periode
                    }

                    // Collect produits for each vendeur
                    produitsDao.getAllAsFlow().collectLatest { produitsList ->
                        // Group produits by vendeur
                        produitsList.forEach { produitModel ->
                            val vendeurId = produitModel.parentkeyID

                            // Find the vendeur in the periodes
                            periodeMap.values.forEach { periode ->
                                periode.vendeursActiveDonsCettePeriode.forEach { (vendeurKey, vendeur) ->
                                    if (vendeurKey == vendeurId) {
                                        // Create produit object
                                        val produit = ProduitsVenduParLui().apply {
                                            this.quantity = produitModel.quantity
                                        }

                                        // Add produit to vendeur
                                        (vendeur.produitsVenduParLui as MutableMap<String, ProduitsVenduParLui>)[produitModel.keyID] = produit
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
                    }
                }
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }
}
