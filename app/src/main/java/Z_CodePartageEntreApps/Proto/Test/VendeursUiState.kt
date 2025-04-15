package Z_CodePartageEntreApps.Proto.Test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Exclude
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class for representing UI state
data class CommandesUiState(
    val periodesVent: SnapshotStateList<PeriodesVent> = mutableStateListOf()
)

// Class representing a period of sales
class PeriodesVent {
    var keyID by mutableStateOf("")
    private var _startTime by mutableStateOf(0L)
    private var _endTime by mutableStateOf(0L)

    // Use custom getters and setters to avoid JVM signature clash
    var startTime: Long
        get() = _startTime
        set(value) {
            _startTime = value
            updateKeyID()
        }

    var endTime: Long
        get() = _endTime
        set(value) {
            _endTime = value
        }

    @get:Exclude
    var vendeursActiveDonsCettePeriode: SnapshotStateList<VendeursActiveDonsCettePeriode> = mutableStateListOf()

    fun updateKeyID() {
        val dateFormat = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(_startTime))
        val timeStr = timeFormat.format(Date(_startTime))
        keyID = "${dateStr}->($timeStr)"
    }
}

// Class representing a vendor active during a sales period
class VendeursActiveDonsCettePeriode {
    var keyID by mutableStateOf("")
    private var _nom by mutableStateOf("")
    private var _startIndex by mutableStateOf(0)

    // Use custom getters and setters to avoid JVM signature clash
    var nom: String
        get() = _nom
        set(value) {
            _nom = value
            updateKeyID()
        }

    var startIndex: Int
        get() = _startIndex
        set(value) {
            _startIndex = value
            updateKeyID()
        }

    @get:Exclude
    var produitsVenduParLui: SnapshotStateList<ProduitsVenduParLui> = mutableStateListOf()

    fun updateKeyID() {
        keyID = "${_startIndex}->($_nom)"
    }
}

// Class representing a product sold by a vendor
class ProduitsVenduParLui {
    var keyID by mutableStateOf("")
    private var _nom by mutableStateOf("")
    private var _startIndex by mutableStateOf(0)
    var quantity by mutableStateOf(0)

    // Use custom getters and setters to avoid JVM signature clash
    var nom: String
        get() = _nom
        set(value) {
            _nom = value
            updateKeyID()
        }

    var startIndex: Int
        get() = _startIndex
        set(value) {
            _startIndex = value
            updateKeyID()
        }

    fun updateKeyID() {
        keyID = "${_startIndex}->($_nom)"
    }
}

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

    // Companion object to hold the test data - Now using function to generate data
    // to ensure fresh instances are created each time the data is loaded
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
        viewModelScope.launch {
            val periodesVentList = mutableStateListOf<PeriodesVent>()

            // Get fresh data using the function
            val testData = getTestPeriodesVent()

            // Convert the test data structure to actual objects
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
        }
    }
}

// Composable to display the CommandesUiState
@Composable
fun CommandesUiStateDisplay(viewModel: CommandesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Périodes de Vente",
        )
        Spacer(modifier = Modifier.height(16.dp))

        uiState.periodesVent.forEach { periode ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Période: ${periode.keyID}",
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vendeurs: ${periode.vendeursActiveDonsCettePeriode.size}",
                    )

                    periode.vendeursActiveDonsCettePeriode.forEach { vendeur ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = vendeur.nom,
                                )

                                vendeur.produitsVenduParLui.forEach { produit ->
                                    Text(
                                        text = "${produit.nom} - Quantité: ${produit.quantity}",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewData() {
    MaterialTheme {
        CommandesUiStateDisplay(viewModel = CommandesViewModel())
    }
}
