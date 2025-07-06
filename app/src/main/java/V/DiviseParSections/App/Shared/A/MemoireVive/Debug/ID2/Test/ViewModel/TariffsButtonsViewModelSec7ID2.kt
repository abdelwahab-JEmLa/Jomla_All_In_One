package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    var produitInfosListDepuitAncienDataBase: SnapshotStateList<_2_1_ProduitsDataBase> = mutableStateListOf(),
    var bonAchatList: List<M8BonVent> = emptyList(),
    var produitAcheteOperationList: List<_1_2_ProduitAcheteOperation> = emptyList(),
    var tariffsList: List<D_TarificationInfos> = emptyList(),

    val loadingProgress: Float = 0f,
    val error: String? = null,
    val isDataSyncing: Boolean = false,
    val isInitializing: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModelSec7ID2(
    val aCentral: ACentralFacade,
    val repo_0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
    private val groupedDataBasesRepository: E_GroupedDataBasesRepositoryNonConnue,
) : ViewModel() {
    val getter = aCentral.getter
    val setter = aCentral.setter

    private val groupedDataBases_modelListFlow = groupedDataBasesRepository.modelListFlow

    private val produitRepository = getter.repoM1ProduitInfos

    private val repoC3_BonVent = getter.id8BonVentRepository

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
        aCentral.focusedVarsHandlerFacade.setter.anulleFocucePourPrixDeM1ProduitFacade()
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

                var tariffsList = emptyList<D_TarificationInfos>()

                launch {
                    groupedDataBases_modelListFlow.collect { dataBaseInfosList ->
                        val newTariffsList = if (dataBaseInfosList.isNotEmpty()) {
                            dataBaseInfosList.first().d_TarificationInfos.toList()
                        } else {
                            emptyList()
                        }

                        val newProduitInfosList = if (dataBaseInfosList.isNotEmpty()) {
                            dataBaseInfosList.first().a_ProduitInfos.toList()
                        } else {
                            emptyList()
                        }

                        // Update tariffs list if changed
                        if (newTariffsList != tariffsList) {
                            tariffsList = newTariffsList
                            _uiState.update { currentState ->
                                currentState.copy(tariffsList = tariffsList)
                            }
                        }
                    }
                }

                bonAchatCollectorJob = launch {
                    try {
                        val initialBonAchatList = repoC3_BonVent.datasValue.toList()

                        _uiState.update { currentState ->
                            currentState.copy(bonAchatList = initialBonAchatList)
                        }

                        // Fixed: Use snapshotFlow to observe changes in onVentData
                        launch {
                            snapshotFlow { repoC3_BonVent.onVentId8BonVent }.collect { activeVentData ->
                                val updatedBonAchatList = repoC3_BonVent.datasValue.toList()
                                _uiState.update { it.copy(bonAchatList = updatedBonAchatList) }
                            }
                        }

                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }
                produitAcheteOperationCollectorJob = launch {
                    try {
                        val initialProduitAcheteList =
                            repositoryC2_ProduitAcheteOperation.modelDatasSnapList.toList()

                        _uiState.update { currentState ->
                            currentState.copy(produitAcheteOperationList = initialProduitAcheteList)
                        }

                        launch {
                            repositoryC2_ProduitAcheteOperation.progressRepo.collect { progress ->
                                if (progress >= 1f) {
                                    delay(100)
                                    val updatedProduitAcheteList =
                                        repositoryC2_ProduitAcheteOperation.modelDatasSnapList.toList()
                                    _uiState.update {
                                        it.copy(produitAcheteOperationList = updatedProduitAcheteList)
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }

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
                bonAchatList = emptyList(),
                produitAcheteOperationList = emptyList(),
                tariffsList = emptyList()
            )
        }

        _uiState.value.produitInfosListDepuitAncienDataBase.clear()

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
