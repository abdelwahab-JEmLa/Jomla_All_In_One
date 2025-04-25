package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel

import V.DiviseParSections.App.SectionID5.Detailes.App.DataBase._01_VentsHistoriques.Models._01_PeriodVentHistorique
import V.DiviseParSections.App.SectionID5.Detailes.App.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent UI state
data class VendeursUiState(
    val vendeurs: List<_1_5_Vendeur> = emptyList(),
    val periodes: List<_1_4_PeriodeVent> = emptyList(),
    val activeVendeurId: Long = 0L,
    val activePeriodeId: Long = 0L,
)

// ViewModel to handle business logic
open class VendeursViewModel(
    private val repository: _0_0_HeadOfRepositorys_Repository,
    private val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository
) : ViewModel() {
    private val list_01_VentsHistoriquesDataBase = repo_01_VentsHistoriquesDataBase_Repository.modelDatasSnapList
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
        val activeVendeurId = repository.repositorys_Model.activeIdDe_1_5_Vendeur
        val activePeriodeId = periodes.lastOrNull()?.vid ?: 0L

        _uiState.value = VendeursUiState(
            vendeurs = vendeurs,
            periodes = periodes,
            activeVendeurId = activeVendeurId,
            activePeriodeId = activePeriodeId
        )
    }

    fun addNewPeriode() {
        viewModelScope.launch {
            val newPeriode = _1_4_PeriodeVent(
                vendeur_ParentVID = uiState.value.activeVendeurId,

            )

            periodeVentRepository.addDataAndReturneItVID(newPeriode) {
                loadData()
                setActivePeriode(it)
            }
        }
    }

    fun addNewPeriodeIn_repo_01_VentsHistoriquesDataBase_Repository() {
        viewModelScope.launch {
            // Find the maximum ID in the existing list and add 1, or use 1 if the list is empty


            list_01_VentsHistoriquesDataBase.add(
                _01_PeriodVentHistorique()
            )
            repo_01_VentsHistoriquesDataBase_Repository.notifierDataChange()
        }
    }

    private fun update_1_5_ceComptVendeurStartAffichePeriod(id: Long): Unit {
        val activeIdDe_1_5_Vendeur = repository.repositorys_Model.activeIdDe_1_5_Vendeur
        val currentVendeur =
            vendeurRepository.modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }

        // Update only if we found the vendor
        currentVendeur?.let { vendeur ->
            val updatedVendeur = vendeur.copy(ceComptVendeurStartAffichePeriod = id)
            vendeurRepository.updateUnSeulData(updatedVendeur)
        }
    }

    fun update_1_5(data: _1_5_Vendeur): Unit {
        repository.upsertUneDataEtReturnVID(data)
    }


    // Add this method to the VendeursViewModel class
    fun getActiveVendeur(): _1_5_Vendeur? {
        val activeIdDe_1_5_Vendeur = repository.repositorys_Model.activeIdDe_1_5_Vendeur
        return vendeurRepository.modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
    }

    fun onUpdateceComptVendeurInsertBonsAchatAuPeriodID(periodId: Long) {
        val activeIdDe_1_5_Vendeur = repository.repositorys_Model.activeIdDe_1_5_Vendeur
        val currentVendeur =
            vendeurRepository.modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }

        // Update only if we found the vendor
        currentVendeur?.let { vendeur ->
            val updatedVendeur = vendeur.copy(ceComptVendeurInsertBonsAchatAuPeriodID = periodId)
            vendeurRepository.updateUnSeulData(updatedVendeur)
        }
    }

    fun setActiveVendeur(id: Long) {
        _uiState.value = _uiState.value.copy(activeVendeurId = id)

        // Call the method on the repository instance with the ID
        repository.updateActiveIdDe_1_5_Vendeur(id)
    }


    fun setActivePeriode(id: Long) {
        _uiState.value = _uiState.value.copy(activePeriodeId = id)
        update_1_5_ceComptVendeurStartAffichePeriod(id)
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
