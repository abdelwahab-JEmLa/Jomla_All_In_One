package V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment.ViewModel

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._00_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._00VentsHistoriquesDataBase_RepositoryImpl
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
    val selectedPeriode: _01_VentsHistoriquesDataBase? = null,
    val searchQuery: String = "",
    val filteredPeriodes: List<_01_VentsHistoriquesDataBase> = emptyList(),
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
    private val repository: _00_VentsHistoriquesDataBase_Repository
) : ViewModel() {
    // Direct access to repository data
    val periodesVente: SnapshotStateList<_01_VentsHistoriquesDataBase> get() = repository.modelDatasSnapList

    // Single UI state flow
    private val _uiState = MutableStateFlow(PeriodeVenteUiState())
    val uiState: StateFlow<PeriodeVenteUiState> = _uiState.asStateFlow()

    init {
        initViewModel()
    }

    private fun initViewModel() {
        observeRepoProgress()
        observeDataChanges()
    }

    // In PeriodeVenteViewModel.kt, add this function to implement search
    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val filtered = if (query.isBlank()) {
                    emptyList()
                } else {
                    repository.modelDatasSnapList.filter {
                        it.dateDebutDeCettePeriode.contains(query, ignoreCase = true) ||
                                it.tempDebutDeCettePeriode.contains(query, ignoreCase = true) ||
                                it.keyID.contains(query, ignoreCase = true)
                    }
                }
                currentState.copy(
                    searchQuery = query,
                    filteredPeriodes = filtered
                )
            }
        }
    }

    // In PeriodeVenteViewModel.kt, add this function to implement view mode switching
    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
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
    fun selectPeriode(periode: _01_VentsHistoriquesDataBase) {
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


    fun notifyDataChanged() {
        repository.notifieDataChange()
    }

    // Clean up when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
        (repository as? _00VentsHistoriquesDataBase_RepositoryImpl)?.cleanup()
    }
}
