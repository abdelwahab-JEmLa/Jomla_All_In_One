package Z_CodePartageEntreApps.Windows.B.Windows.Options.Ui

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel to handle business logic
open class VendeursViewModel(
    private val repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendeursUiState())
    open val uiState: StateFlow<VendeursUiState> = _uiState.asStateFlow()

    private val vendeurRepository = repository.repositorys_Model.repository_1_5_Vendeur
    private val periodeVentRepository = repository.repositorys_Model.repository_1_4_PeriodeVent

    init {
        // Initial load attempt
        loadData()

        // Set up collection of the progress flow
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                if (progress == 1f) {
                    loadData()
                }
            }
        }
    }

    private fun loadData() {
        val vendeurs = vendeurRepository.modelDatasSnapList
        val periodes = periodeVentRepository.modelDatasSnapList
        val activeVendeurId = periodes.firstOrNull()?.vid ?: 0L
        val activePeriodeId = periodes.lastOrNull()?.vid ?: 0L

        _uiState.value = VendeursUiState(
            vendeurs = vendeurs,
            periodes = periodes,
            activeVendeurId = activeVendeurId,
            activePeriodeId = activePeriodeId
        )
    }

    fun setActiveVendeur(id: Long) {
        _uiState.value = _uiState.value.copy(activeVendeurId = id)
    }

    fun setActivePeriode(id: Long) {
        _uiState.value = _uiState.value.copy(activePeriodeId = id)
    }

    // Only for development/testing
    fun addTestData() {
        vendeurRepository.addDataAndReturneItVID(_1_5_Vendeur(nom = "W"))
        vendeurRepository.addDataAndReturneItVID(_1_5_Vendeur(nom = "M"))
        periodeVentRepository.addDataAndReturneItVID(_1_4_PeriodeVent(heurDebutInString = "1:mm"))
        periodeVentRepository.addDataAndReturneItVID(_1_4_PeriodeVent(heurDebutInString = "2:mm"))
        loadData()
    }
}
