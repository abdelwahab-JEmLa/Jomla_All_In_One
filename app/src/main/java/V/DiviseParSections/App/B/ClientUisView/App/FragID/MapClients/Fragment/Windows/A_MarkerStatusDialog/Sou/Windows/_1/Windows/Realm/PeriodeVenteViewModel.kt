// 1. Modification du PeriodeVenteViewModel.kt
package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_Repository
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    // Utilisons directement la SnapshotStateList du repository au lieu de créer une copie
    val periodesVente: SnapshotStateList<_01_PeriodesVent>
        get() = repository.modelDatasSnapList

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPeriodesVente()
        observeRepoProgress()
    }

    private fun observeRepoProgress() {
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                _isLoading.value = progress < 1.0f
            }
        }
    }

    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
            // Note: We don't need to update _periodesVente anymore since we're using repository.modelDatasSnapList directly
            _isLoading.value = false
        }
    }

    fun selectPeriode(periode: _01_PeriodesVent) {
        _selectedPeriode.value = periode
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
            _isLoading.value = false
        }
    }
}
