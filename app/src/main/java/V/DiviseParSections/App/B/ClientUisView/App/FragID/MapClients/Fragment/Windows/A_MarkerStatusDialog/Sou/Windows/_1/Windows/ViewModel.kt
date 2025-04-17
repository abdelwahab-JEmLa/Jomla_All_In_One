package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows


import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent UI state
data class UiState(
    val vendeurs: List<_1_5_Vendeur> = emptyList(),
    val periodes: List<_1_4_PeriodeVent> = emptyList(),
    val activeVendeurId: Long = 0L,
    val activePeriodeId: Long = 0L,
)

// ViewModel to handle business logic
open class ViewModel(
    private val repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    open val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
        val activeVendeurId = repository.repositorys_Model.activeIdDe_1_5_Vendeur
        val activePeriodeId = periodes.lastOrNull()?.vid ?: 0L

        _uiState.value = UiState(
            vendeurs = vendeurs,
            periodes = periodes,
            activeVendeurId = activeVendeurId,
            activePeriodeId = activePeriodeId
        )
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
