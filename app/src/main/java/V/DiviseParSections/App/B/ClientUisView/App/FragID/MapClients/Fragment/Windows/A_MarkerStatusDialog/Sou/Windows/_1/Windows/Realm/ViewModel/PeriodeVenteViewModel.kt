package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_RepositoryImpl
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PeriodeVenteViewModel"

// UI State class that contains all UI-related state
data class PeriodeVenteUiState(
    val isLoading: Boolean = false,
    val selectedPeriode: _01_PeriodesVent? = null,
    val searchQuery: String = "",
    val filteredPeriodes: List<_01_PeriodesVent> = emptyList(),
    val viewMode: ViewMode = ViewMode.LIST,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

// Enum for different view modes
enum class ViewMode {
    LIST,     // Standard list view of periods
    DETAIL,   // Detailed view of a single period
    CALENDAR, // Calendar view showing periods by date
    ANALYTICS // Analytics/statistics view
}

class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    // Direct access to repository data
    private val periodesVente: SnapshotStateList<_01_PeriodesVent> get() = repository.modelDatasSnapList

    // Single UI state flow
    private val _uiState = MutableStateFlow(PeriodeVenteUiState())
    val uiState: StateFlow<PeriodeVenteUiState> = _uiState.asStateFlow()

    init {
        initViewModel()
    }

    private fun initViewModel() {
        observeRepoProgress()
        observeDataChanges()
        updateFilteredPeriods()
    }

    private fun observeRepoProgress() {
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = progress < 1.0f
                    )
                }

                if (progress >= 1.0f) {
                    loadPeriodesVente()
                }
            }
        }
    }

    private fun observeDataChanges() {
        viewModelScope.launch {
            repository.dataChangedEvent.collect { timestamp ->
                if (timestamp > 0) {
                    loadPeriodesVente()
                }
            }
        }
    }

    // Load data function
    private fun loadPeriodesVente() {
        viewModelScope.launch {
            try {
                // Check if we have a selected period and update it if it exists
                val currentSelection = _uiState.value.selectedPeriode
                val updatedSelection = if (currentSelection != null) {
                    // Try to find updated version of currently selected period
                    periodesVente.find { it.keyID == currentSelection.keyID }
                } else {
                    null
                }

                // Update state with potentially updated selection
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedPeriode = updatedSelection,
                        error = null
                    )
                }

                // Update filtered list based on current search query
                updateFilteredPeriods()
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Erreur lors du chargement des données: ${e.message}"
                    )
                }
            }
        }
    }

    // Function to select a period
    fun selectPeriode(periode: _01_PeriodesVent) {
        _uiState.update { it.copy(
            selectedPeriode = periode,
            viewMode = ViewMode.DETAIL
        ) }
    }

    // Function to clear selection
    fun clearSelection() {
        _uiState.update { it.copy(
            selectedPeriode = null,
            viewMode = ViewMode.LIST
        ) }
    }

    // Function to update search query
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredPeriods()
    }

    // Function to change view mode
    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    // Refresh data from repository
    fun refreshData() {
        _uiState.update { it.copy(isRefreshing = true) }
        repository.notifieDataChange()
    }

    // Function to filter periods based on search query
    private fun updateFilteredPeriods() {
        val query = _uiState.value.searchQuery.trim().lowercase()

        if (query.isEmpty()) {
            _uiState.update { it.copy(
                filteredPeriodes = periodesVente.toList(),
                isRefreshing = false
            ) }
            return
        }

        val filtered = periodesVente.filter { periode ->
            // Search in date or time
            periode.dateDebutDeCettePeriode.lowercase().contains(query) ||
                    periode.tempDebutDeCettePeriode.lowercase().contains(query) ||
                    // Search in vendors
                    periode.vendeurs.any { vendeur ->
                        vendeur.nomVendeur.lowercase().contains(query) ||
                                // Search in products
                                vendeur.acheteurs.any { acheteur ->
                                    acheteur.startDesignation.lowercase().contains(query) ||
                                            // Search in products
                                            acheteur.child_14Produits.any { produit ->
                                                produit.startDesignation.lowercase().contains(query)
                                            }
                                }
                    }
        }

        _uiState.update { it.copy(
            filteredPeriodes = filtered,
            isRefreshing = false
        ) }
    }

    fun notifyDataChanged() {
        repository.notifieDataChange()
    }

    // Clean up when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
        (repository as? _01_PeriodesVent_RepositoryImpl)?.cleanup()
    }
}
