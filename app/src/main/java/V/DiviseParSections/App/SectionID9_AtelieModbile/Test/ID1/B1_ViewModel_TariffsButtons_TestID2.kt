package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
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
    var produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase> = mutableStateListOf(),
    var bonAchatList: List<C3_BonAchate> = emptyList(),
    var tariffsList: List<D_TarificationInfos> = emptyList(),

    val loadingProgress: Float = 0f,
    val error: String? = null,
    val sqlProgress: Float = 0f,
    val produitProgress: Float = 0f,
    val bonAchatProgress: Float = 0f,
    val isDataSyncing: Boolean = false,
    val isInitializing: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModel_TestID2(
    val repo_0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys,
    private val sqlRepository: E_InfosSqlDataBasesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null
    private var bonAchatCollectorJob: Job? = null
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
                val tariffsRepo = sqlRepository.modelListFlow
                val produitRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                    ._2_1_ProduitsDataBase_Repository
                val repoC3_BonAchat = repo_0_0_HeadSQLRepositorys
                    .repositorys_Model
                    .repository_1_3_TransactionCommercial

                progressJob = launch {
                    combine(
                        produitRepository.progressRepo,
                        repoC3_BonAchat.progressRepo,
                        sqlRepository.progressRepo
                    ) { produitProgress, bonAchatProgress, sqlProgress ->

                        val validProduitProgress = produitProgress.coerceIn(0f, 1f)
                        val validBonAchatProgress = bonAchatProgress.coerceIn(0f, 1f)
                        val validSqlProgress = sqlProgress.coerceIn(0f, 1f)

                        val totalProgress = (validProduitProgress + validBonAchatProgress + validSqlProgress) / 3f

                        _uiState.update { currentState ->
                            currentState.copy(
                                loadingProgress = totalProgress.coerceIn(0f, 1f),
                                sqlProgress = validSqlProgress,
                                produitProgress = validProduitProgress,
                                bonAchatProgress = validBonAchatProgress,
                                isDataSyncing = totalProgress < 1f,
                                hasStartedLoading = true
                            )
                        }

                        Triple(validProduitProgress, validBonAchatProgress, validSqlProgress)
                    }.collect { (produitProg, bonAchatProg, sqlProg) ->
                        if (produitProg >= 1f && bonAchatProg >= 1f && sqlProg >= 1f) {
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
                launch {
                    tariffsRepo.collect { dataBaseInfosList ->
                        val newTariffsList = if (dataBaseInfosList.isNotEmpty()) {
                            dataBaseInfosList.first().d_TarificationInfos.toList()
                        } else {
                            emptyList()
                        }

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
                        val initialBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()

                        _uiState.update { currentState ->
                            currentState.copy(bonAchatList = initialBonAchatList)
                        }

                        launch {
                            repoC3_BonAchat.progressRepo.collect { progress ->
                                if (progress >= 1f) {
                                    delay(100)
                                    val updatedBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()
                                    _uiState.update {
                                        it.copy(bonAchatList = updatedBonAchatList)
                                    }
                                }
                            }
                        }

                        launch {
                            repoC3_BonAchat.activeId.collect { activeId ->
                                val updatedBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()
                                _uiState.update { it.copy(bonAchatList = updatedBonAchatList) }
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
                                currentState.produitInfosList.clear()
                                currentState.produitInfosList.addAll(productsList)
                                currentState
                            }
                        }

                        produitRepository.progressRepo.collect { progress ->
                            if (progress >= 1f) {
                                val updatedProductsList = produitRepository.modelDatasSnapList.toList()
                                if (updatedProductsList.isNotEmpty()) {
                                    _uiState.update { currentState ->
                                        currentState.produitInfosList.clear()
                                        currentState.produitInfosList.addAll(updatedProductsList)
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
                tariffsList = emptyList()
            )
        }

        _uiState.value.produitInfosList.clear()

        loadTariffs()
    }

    fun getSyncStatus(): String {
        val state = _uiState.value
        return when {
            state.isInitializing -> "Initializing..."
            !state.hasStartedLoading -> "Preparing to load..."
            state.error != null -> "Error: ${state.error}"
            state.isDataSyncing -> {
                when {
                    state.sqlProgress > 0f && state.sqlProgress < 1f -> "Syncing database..."
                    state.produitProgress > 0f && state.produitProgress < 1f -> "Loading products..."
                    state.bonAchatProgress > 0f && state.bonAchatProgress < 1f -> "Loading purchase orders..."
                    else -> "Synchronizing data..."
                }
            }
            state.loadingProgress >= 1f -> "Loading completed"
            else -> "Ready"
        }
    }


    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        progressJob?.cancel()
    }
}
