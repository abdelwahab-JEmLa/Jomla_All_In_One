package Z_CodePartageEntreApps.Proto.Test.FragID1.DemiNoSQL.Fragment

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class for representing UI state
data class CommandesUiState(
    val periodesVent: SnapshotStateList<PeriodesVent> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class CommandesViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommandesUiState())
    open val uiState: StateFlow<CommandesUiState> = _uiState.asStateFlow()

    // Data classes for test data structure - moved before companion object to be accessible
    data class TestPeriodeData(
        val relativePastHours: Int,
        val durationHours: Int = 1,
        val vendeurs: List<TestVendeurData>
    )

    data class TestVendeurData(
        val nom: String,
        val index: Int,
        val produits: List<TestProduitData>
    )

    data class TestProduitData(
        val nom: String,
        val index: Int,
        val quantity: Int
    )

    companion object {
        // Function to get test data instead of static property
        fun getTestPeriodesVent(): List<TestPeriodeData> {
            return listOf(
                TestPeriodeData(
                    relativePastHours = 1, // 1 hour ago
                    vendeurs = listOf(
                        TestVendeurData(
                            nom = "Vendeur 1",
                            index = 1,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 105),      // <-- Changed from 5 to 55
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        ),
                        TestVendeurData(
                            nom = "Vendeur 2",
                            index = 2,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 5),
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        )
                    )
                ),
                TestPeriodeData(
                    relativePastHours = 2, // 2 hours ago
                    durationHours = 1,
                    vendeurs = listOf(
                        TestVendeurData(
                            nom = "Vendeur 1",
                            index = 1,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 5),
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        ),
                        TestVendeurData(
                            nom = "Vendeur 2",
                            index = 2,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 5),
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        )
                    )
                )
                ,
                TestPeriodeData(
                    relativePastHours = 3, // 2 hours ago
                    durationHours = 3,
                    vendeurs = listOf(
                        TestVendeurData(
                            nom = "Vendeur 1",
                            index = 1,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 5),
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        ),
                        TestVendeurData(
                            nom = "Vendeur 2",
                            index = 2,
                            produits = listOf(
                                TestProduitData("Produit A", 1, 5),
                                TestProduitData("Produit B", 2, 3),
                                TestProduitData("Produit C", 3, 8)
                            )
                        )
                    )
                )
            )
        }
    }

    init {
        val loadFromTest = true
        if (loadFromTest) {
            loadFromTestData()
        } else {
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // Here you would populate data from your repositories
                val periodesVent = fetchPeriodesVent()

                _uiState.value = CommandesUiState(
                    periodesVent = periodesVent
                )
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchPeriodesVent(): SnapshotStateList<PeriodesVent> {
        // This would be implemented to fetch actual data from your repository
        // For now returning a placeholder list
        return mutableStateListOf()
    }

    // Public method to reload data - can be called when data changes
    fun reloadTestData() {
        loadFromTestData()
    }

    // Load test data from the companion object function
    private fun loadFromTestData() {
      /*  viewModelScope.launch {
            val periodesVentList = mutableStateListOf<PeriodesVent>()

            // Get fresh data using the function
            val testData = getTestPeriodesVent()

            testData.forEach { periodeData ->
                val currentTime = System.currentTimeMillis()
                val pastHoursInMillis = periodeData.relativePastHours * 3600000L
                val durationInMillis = periodeData.durationHours * 3600000L

                val startTime = currentTime - pastHoursInMillis
                val endTime = startTime + durationInMillis

                val periode = PeriodesVent().apply {
                    this.startTime = startTime
                    this.endTime = endTime

                    // Add vendors to this period
                    periodeData.vendeurs.forEach { vendeurData ->
                        val vendeur = VendeursActiveDonsCettePeriode().apply {
                            this.nom = vendeurData.nom
                            this.startIndex = vendeurData.index

                            // Add products to this vendor
                            vendeurData.produits.forEach { produitData ->
                                val produit = ProduitsVenduParLui().apply {
                                    this.nom = produitData.nom
                                    this.startIndex = produitData.index
                                    this.quantity = produitData.quantity
                                }
                                produitsVenduParLui.add(produit)
                            }
                        }
                        vendeursActiveDonsCettePeriode.add(vendeur)
                    }
                }

                periodesVentList.add(periode)
            }

            // Create a new CommandesUiState instance to trigger recomposition
            _uiState.value = CommandesUiState(periodesVent = periodesVentList)
        }   */
    }
}

