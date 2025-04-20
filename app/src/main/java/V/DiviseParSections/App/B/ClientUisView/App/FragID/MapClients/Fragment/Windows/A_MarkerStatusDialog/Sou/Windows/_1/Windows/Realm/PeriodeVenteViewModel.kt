package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PeriodeVenteViewModel"

open class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    // Direct access to repository data
    val periodesVente: SnapshotStateList<_01_PeriodesVent> get() = repository.modelDatasSnapList

    // UI state flows
    private val _uiState = MutableStateFlow(0)
    val uiState: StateFlow<Int> = _uiState.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // New state for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered periods based on search
    private val _filteredPeriodes = MutableStateFlow<List<_01_PeriodesVent>>(emptyList())
    val filteredPeriodes: StateFlow<List<_01_PeriodesVent>> = _filteredPeriodes.asStateFlow()

    init {
        initViewModel()
    }

    private fun initViewModel() {
        observeRepoProgress()
        updateFilteredPeriods()
    }

    private fun observeRepoProgress() {
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                _isLoading.value = progress < 1.0f
                if (progress >= 1.0f) {
                    loadPeriodesVente()
                }
            }
        }
    }

    // New function to load data
    private fun loadPeriodesVente() {
        viewModelScope.launch {
            // Reset selection and update filtered list
            _selectedPeriode.value = null
            updateFilteredPeriods()
        }
    }

    // New function to refresh data from repository
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
        }
    }

    // Function to select a period
    fun selectPeriode(periode: _01_PeriodesVent) {
        _selectedPeriode.value = periode
    }

    // Function to clear selection
    fun clearSelection() {
        _selectedPeriode.value = null
    }

    // Function to update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredPeriods()
    }

    // Function to filter periods based on search query
    private fun updateFilteredPeriods() {
        val query = _searchQuery.value.trim().lowercase()

        if (query.isEmpty()) {
            _filteredPeriodes.value = periodesVente.toList()
            return
        }

        _filteredPeriodes.value = periodesVente.filter { periode ->
            // Search in date or time
            periode.dateDebutDeCettePeriode.lowercase().contains(query) ||
                    periode.tempDebutDeCettePeriode.lowercase().contains(query) ||
                    // Search in vendors
                    periode.vendeurs.any { vendeur ->
                        vendeur.nom.lowercase().contains(query) ||
                                // Search in products
                                vendeur.produits.any { produit ->
                                    produit.nom.lowercase().contains(query)
                                }
                    }
        }
    }

    // Function to update UI state
    fun updateUiState(newState: Int) {
        _uiState.value = newState
    }
}
