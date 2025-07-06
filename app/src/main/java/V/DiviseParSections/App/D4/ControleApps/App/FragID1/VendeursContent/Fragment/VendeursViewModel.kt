package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent UI state
data class VendeursUiState(
    val vendeurs: List<Z_AppCompt> = emptyList(),
    val periodes: List<MVentPeriode> = emptyList(),
    val activeVendeurKeyId: String = "",
    val activePeriodeId: Long = 0L,
)

// ViewModel to handle business logic
open class VendeursViewModel(
    val  aCentralFacade: ACentralFacade,
    val getter: AGetter,
    private val repository: GroupeRepositorysProtoAvJuin3,
    private val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository
) : ViewModel() {

    private val list_01_VentsHistoriquesDataBase = repo_01_VentsHistoriquesDataBase_Repository.modelDatasSnapList
    private val _uiState = MutableStateFlow(VendeursUiState())
    open val uiState: StateFlow<VendeursUiState> = _uiState.asStateFlow()

    private val vendeurRepository = getter.repo9AppCompt
    private val periodeVentRepository = repository.repositorys_Model.repositoryMVentPeriode

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
        val vendeurs = vendeurRepository.datasValue
        val periodes = periodeVentRepository.modelDatasSnapList
        val activeVendeurId = getter.repo9AppCompt.currentAppCompt?.keyID
        val activePeriodeId = periodes.lastOrNull()?.vid ?: 0L

        _uiState.value = activeVendeurId?.let {
            VendeursUiState(
                vendeurs = vendeurs,
                periodes = periodes,
                activeVendeurKeyId = it,
                activePeriodeId = activePeriodeId
            )
        }!!
    }

    fun addNewPeriode() {
        viewModelScope.launch {
            val newPeriode = MVentPeriode(
                parentM9AppComptKeyID = uiState.value.activeVendeurKeyId,
            )

            periodeVentRepository.addDataAndReturneItVID(newPeriode) {
                loadData()
                setActivePeriode(it)
            }
        }
    }

    fun addNewPeriodeIn_repo_01_VentsHistoriquesDataBase_Repository() {
        viewModelScope.launch {
            // Find the maximum ID in the existing list and upsert 1, or use 1 if the list is empty


            list_01_VentsHistoriquesDataBase.add(
                _01_PeriodVentHistorique()
            )
            repo_01_VentsHistoriquesDataBase_Repository.notifierDataChange()
        }
    }

    private fun update_1_5_ceComptVendeurStartAffichePeriod(id: Long): Unit {

    }

    fun update_1_5(data: Z_AppCompt): Unit {
        repository.upsertUneDataEtReturnVID_1_5_Vendeur(data)
    }


    // Add this method to the VendeursViewModel class
    fun getActiveVendeur(): Z_AppCompt? =
        getter.repo9AppCompt.currentAppCompt


    fun onUpdateceComptVendeurInsertBonsAchatAuPeriodID(periodId: Long) {
    }

    fun setActiveVendeur(id: Long) {
        _uiState.value = _uiState.value.copy(activeVendeurKeyId = id.toString())

        // Call the method on the repository instance with the ID
      //  repository.updateActiveIdDe_1_5_Vendeur(keyID)
    }


    fun setActivePeriode(id: Long) {
        _uiState.value = _uiState.value.copy(activePeriodeId = id)
        update_1_5_ceComptVendeurStartAffichePeriod(id)
    }

}
