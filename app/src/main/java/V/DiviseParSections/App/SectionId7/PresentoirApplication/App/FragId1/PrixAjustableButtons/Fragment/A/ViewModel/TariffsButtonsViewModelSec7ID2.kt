package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(

    val loadingProgress: Float = 0f,
    val error: String? = null,
    val isDataSyncing: Boolean = false,
    val isInitializing: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModelSec7ID2(
    val aCentralFacade: ACentralFacade,
    val repo_0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
    private val groupedDataBasesRepository: E_GroupedDataBasesRepositoryNonConnue,
) : ViewModel() {
    val getter = aCentralFacade.get
    val setter = aCentralFacade.set

    private val groupedDataBases_modelListFlow = groupedDataBasesRepository.modelListFlow

    private val produitRepository = getter.repoM1ProduitInfos

    private val repoC3_BonVent = getter.repo8BonVent

    private val repositoryC2_ProduitAcheteOperation = repo_0_0_HeadSQLRepositorys
        .repositorys_Model
        .repositoryC2_ProduitAcheteOperation

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null
    private var bonAchatCollectorJob: Job? = null
    private var produitAcheteOperationCollectorJob: Job? = null
    private var progressJob: Job? = null

    init {
        _uiState.update {
            it.copy(
                isInitializing = true,
                hasStartedLoading = false,
                isDataSyncing = false,
                loadingProgress = 0f
            )
        }
        loadTariffs()
    }

    fun updateListRelativeVentCouleurPrixVent(
        m1produitInfos: ArticlesBasesStatsTable?,
        newPrix: Double,
        listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>
    ): Unit {
        setter.updateListRelativeVentCouleurPrixVent(
            listFocusedM10OpeVentCouleurParPrixDifineur,
            m1produitInfos,
            newPrix
        )
        aCentralFacade.focusedActiveValuesFacade.set.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
    }


    private fun loadTariffs() {
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        produitAcheteOperationCollectorJob?.cancel()
        progressJob?.cancel()

        loadingJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingProgress = 0f,
                    isDataSyncing = true,
                    hasStartedLoading = true,
                    isInitializing = false,
                    error = null
                )
            }

            try {

                delay(100)

                launch {
                    try {

                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        loadingProgress = 0f,
                        isDataSyncing = false,
                        hasStartedLoading = true
                    )
                }
            }
        }
    }

    fun refreshTariffs() {
        _uiState.update {
            it.copy(
                loadingProgress = 0f,
                isDataSyncing = true,
                hasStartedLoading = false,
                error = null,
            )
        }


        loadTariffs()
    }

    fun getSyncStatus(): String {
        val state = _uiState.value
        return when {
            state.isInitializing -> "Initializing..."
            !state.hasStartedLoading -> "Preparing to load..."
            state.error != null -> "Error: ${state.error}"
            state.isDataSyncing -> "Synchronizing data..."
            state.loadingProgress >= 1f -> "Loading completed"
            else -> "Ready"
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        produitAcheteOperationCollectorJob?.cancel()
        progressJob?.cancel()
    }

    fun deleteVents(parentProduitOldId: Long) {
        setter.deleteVents(parentProduitOldId)

    }


}
