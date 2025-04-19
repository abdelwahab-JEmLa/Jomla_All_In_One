package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_Repository
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    private val _periodesVente = MutableStateFlow(mutableStateListOf<_01_PeriodesVent>())

    val periodesVente: StateFlow<List<_01_PeriodesVent>> = _periodesVente.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPeriodesVente()
    }


    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            // Update the periodesVente flow with data from repository
            _periodesVente.value = repository.modelDatasSnapList
            _isLoading.value = false
        }
    }

    fun selectPeriode(periode: _01_PeriodesVent) {
        _selectedPeriode.value = periode
    }
}
