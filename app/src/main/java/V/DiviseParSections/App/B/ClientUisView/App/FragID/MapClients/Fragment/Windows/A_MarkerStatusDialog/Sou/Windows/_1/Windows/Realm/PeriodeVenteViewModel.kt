package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    private val _periodesVente = MutableStateFlow<List<_01_PeriodesVent>>(emptyList())
    val periodesVente: StateFlow<List<_01_PeriodesVent>> = _periodesVente.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPeriodesVente()

        viewModelScope.launch {
            createTestDataIfEmpty()
        }
    }

    private fun createTestDataIfEmpty() {
        if (repository.modelDatasSnapList.isEmpty()) {
            viewModelScope.launch {
                // Create sample data
                val testPeriode = _01_PeriodesVent().apply {
                    keyID = "test_periode_${System.currentTimeMillis()}"
                    dateDebutDeCettePeriode = "2025_04_19"
                    tempDebutDeCettePeriode = "10:00"

                    // Add a test vendeur
                    val testVendeur = Vendeur().apply {
                        keyID = "test_vendeur_${System.currentTimeMillis()}"
                        startIndex = 0
                        nom = "Jean Dupont"

                        // Add test products
                        produits.add(Produit().apply {
                            keyID = "test_produit_1_${System.currentTimeMillis()}"
                            startIndex = 0
                            nom = "T-shirt"
                            quantity = 10
                        })

                        produits.add(Produit().apply {
                            keyID = "test_produit_2_${System.currentTimeMillis()}"
                            startIndex = 1
                            nom = "Pantalon"
                            quantity = 5
                        })
                    }

                    vendeurs.add(testVendeur)
                }

                repository.modelDatasSnapList.add(testPeriode)
            }
        }

        // Load data after creating test data
        loadPeriodesVente()
    }

    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            // Update the periodesVente flow with data from repository
            _periodesVente.value = repository.modelDatasSnapList.toList()
            _isLoading.value = false
        }
    }

    fun selectPeriode(periode: _01_PeriodesVent) {
        _selectedPeriode.value = periode
    }
}
