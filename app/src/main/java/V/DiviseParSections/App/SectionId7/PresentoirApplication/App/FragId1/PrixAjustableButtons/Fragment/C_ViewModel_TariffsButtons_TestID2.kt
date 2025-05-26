package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.E_GroupedDataBasesRepository
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    var produitInfosListDepuitAncienDataBase: SnapshotStateList<_2_1_ProduitsDataBase> = mutableStateListOf(),
    var produitInfosList: SnapshotStateList<A_ProduitInfos> = mutableStateListOf(),
    var bonAchatList: List<C3_BonAchate> = emptyList(),
    var produitAcheteOperationList: List<_1_2_ProduitAcheteOperation> = emptyList(),
    var tariffsList: List<D_TarificationInfos> = emptyList(),

    val loadingProgress: Float = 0f,
    val error: String? = null,
    val isDataSyncing: Boolean = false,
    val isInitializing: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModel_TestID2(
    val repo_0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys,
    private val groupedDataBasesRepository: E_GroupedDataBasesRepository,
) : ViewModel() {
    private val groupedDataBases_modelListFlow = groupedDataBasesRepository.modelListFlow

    private val produitRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
        ._2_1_ProduitsDataBase_Repository

    private val repoC3_BonVent = repo_0_0_HeadSQLRepositorys
        .repositorys_Model
        .repository_1_3_TransactionCommercial

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
                progressJob = launch {
                    combine(
                        produitRepository.progressRepo,
                        repoC3_BonVent.progressRepo,
                        repositoryC2_ProduitAcheteOperation.progressRepo,
                        groupedDataBasesRepository.mainProgressRepo
                    ) { produitProgress, bonAchatProgress, produitAcheteProgress, sqlProgress ->

                        val validProduitProgress = produitProgress.coerceIn(0f, 1f)
                        val validBonAchatProgress = bonAchatProgress.coerceIn(0f, 1f)
                        val validProduitAcheteProgress = produitAcheteProgress.coerceIn(0f, 1f)
                        val validSqlProgress = sqlProgress.coerceIn(0f, 1f)

                        val totalProgress = (validProduitProgress + validBonAchatProgress + validProduitAcheteProgress + validSqlProgress) / 4f

                        _uiState.update { currentState ->
                            currentState.copy(
                                loadingProgress = totalProgress.coerceIn(0f, 1f),
                                isDataSyncing = totalProgress < 1f,
                                hasStartedLoading = true
                            )
                        }

                        Pair(Pair(validProduitProgress, validBonAchatProgress), Pair(validProduitAcheteProgress, validSqlProgress))
                    }.collect { (firstPair, secondPair) ->
                        val (produitProg, bonAchatProg) = firstPair
                        val (produitAcheteProg, sqlProg) = secondPair

                        if (produitProg >= 1f && bonAchatProg >= 1f && produitAcheteProg >= 1f && sqlProg >= 1f) {
                            _uiState.update {
                                it.copy(
                                    isDataSyncing = false,
                                    loadingProgress = 1f
                                )
                            }
                        }
                    }
                }

                var tariffsList = emptyList<D_TarificationInfos>()
                var a_produitList = emptyList<A_ProduitInfos>()

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

                        // Update produit infos list if changed
                        if (newProduitInfosList != a_produitList) {
                            a_produitList = newProduitInfosList
                            _uiState.update { currentState ->
                                currentState.produitInfosList.clear()
                                currentState.produitInfosList.addAll(a_produitList)
                                currentState
                            }
                        }
                    }
                }

                bonAchatCollectorJob = launch {
                    try {
                        val initialBonAchatList = repoC3_BonVent.modelDatasSnapList.toList()

                        _uiState.update { currentState ->
                            currentState.copy(bonAchatList = initialBonAchatList)
                        }

                        launch {
                            repoC3_BonVent.progressRepo.collect { progress ->
                                if (progress >= 1f) {
                                    delay(100)
                                    val updatedBonAchatList = repoC3_BonVent.modelDatasSnapList.toList()
                                    _uiState.update {
                                        it.copy(bonAchatList = updatedBonAchatList)
                                    }
                                }
                            }
                        }

                        launch {
                            repoC3_BonVent.activeId.collect { activeId ->
                                val updatedBonAchatList = repoC3_BonVent.modelDatasSnapList.toList()
                                _uiState.update { it.copy(bonAchatList = updatedBonAchatList) }
                            }
                        }

                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }

                produitAcheteOperationCollectorJob = launch {
                    try {
                        val initialProduitAcheteList = repositoryC2_ProduitAcheteOperation.modelDatasSnapList.toList()

                        _uiState.update { currentState ->
                            currentState.copy(produitAcheteOperationList = initialProduitAcheteList)
                        }

                        launch {
                            repositoryC2_ProduitAcheteOperation.progressRepo.collect { progress ->
                                if (progress >= 1f) {
                                    delay(100)
                                    val updatedProduitAcheteList = repositoryC2_ProduitAcheteOperation.modelDatasSnapList.toList()
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
                        val repoSize = produitRepository.modelDatasSnapList.size

                        if (repoSize > 0) {
                            val productsList = produitRepository.modelDatasSnapList.toList()

                            _uiState.update { currentState ->
                                currentState.produitInfosListDepuitAncienDataBase.clear()
                                currentState.produitInfosListDepuitAncienDataBase.addAll(productsList)
                                currentState
                            }
                        }

                        produitRepository.progressRepo.collect { progress ->
                            if (progress >= 1f) {
                                val updatedProductsList = produitRepository.modelDatasSnapList.toList()
                                if (updatedProductsList.isNotEmpty()) {
                                    _uiState.update { currentState ->
                                        currentState.produitInfosListDepuitAncienDataBase.clear()
                                        currentState.produitInfosListDepuitAncienDataBase.addAll(updatedProductsList)
                                        currentState
                                    }
                                }
                            }
                        }

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
        _uiState.value.produitInfosList.clear() // Also clear the new produit infos list

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
}
