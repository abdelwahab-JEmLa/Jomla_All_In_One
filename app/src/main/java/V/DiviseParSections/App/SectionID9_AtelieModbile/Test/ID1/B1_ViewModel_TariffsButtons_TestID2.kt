package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.InfosSqlDataBasesRepository
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
         //<--
         //TODO(1): ici aussi 
data class UiState(
    var produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase> = mutableStateListOf(),
    var bonAchatList: List<C3_BonAchate> = emptyList(),
    var tariffsList: List<D_TarificationInfos> = emptyList(),
    val loadingProgress: Float = 0f,
    val error: String? = null,
    // Enhanced progress tracking
    val sqlProgress: Float = 0f,
    val produitProgress: Float = 0f,
    val bonAchatProgress: Float = 0f,
    val isDataSyncing: Boolean = false,
    // Added initialization state
    val isInitializing: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModel_TestID2(
    val repo_0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys,
    private val sqlRepository: InfosSqlDataBasesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null
    private var bonAchatCollectorJob: Job? = null
    private var progressJob: Job? = null

    init {
        // Initialize with proper state
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
            // Set initial loading state
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

                // Enhanced progress tracking with proper initialization
                progressJob = launch {
                    // Collect individual progress streams
                    combine(
                        produitRepository.progressRepo,
                        repoC3_BonAchat.progressRepo,
                        sqlRepository.progressRepo
                    ) { produitProgress, bonAchatProgress, sqlProgress ->

                        // Ensure we start showing progress immediately
                        val validProduitProgress = produitProgress.coerceIn(0f, 1f)
                        val validBonAchatProgress = bonAchatProgress.coerceIn(0f, 1f)
                        val validSqlProgress = sqlProgress.coerceIn(0f, 1f)

                        // Calculate weighted combined progress
                        val totalProgress = (validProduitProgress + validBonAchatProgress + validSqlProgress) / 3f

                        // Update UI state with individual and combined progress
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
                        // Log progress for debugging
                        println("Progress Update - Produit: ${(produitProg * 100).toInt()}%, BonAchat: ${(bonAchatProg * 100).toInt()}%, SQL: ${(sqlProg * 100).toInt()}%")

                        // Check if all operations are complete
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

                // Collect tariffs with progress updates
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

                // Enhanced BonAchat collection with progress tracking
                bonAchatCollectorJob = launch {
                    try {
                        // First try to get initial data
                        val initialBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()

                        _uiState.update { currentState ->
                            currentState.copy(bonAchatList = initialBonAchatList)
                        }

                        println("Initial BonAchat list size: ${initialBonAchatList.size}")

                        // Monitor progress repository to trigger data refresh when ready
                        launch {
                            repoC3_BonAchat.progressRepo.collect { progress ->
                                println("BonAchat progress: ${(progress * 100).toInt()}%")

                                if (progress >= 1f) {
                                    delay(100) // Small delay to ensure data is available
                                    val updatedBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()

                                    println("Updated BonAchat list size after progress completion: ${updatedBonAchatList.size}")

                                    _uiState.update {
                                        it.copy(bonAchatList = updatedBonAchatList)
                                    }
                                }
                            }
                        }

                        // Continue monitoring for activeId changes
                        launch {
                            repoC3_BonAchat.activeId.collect { activeId ->
                                println("BonAchat activeId changed: $activeId")
                                val updatedBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()
                                _uiState.update { it.copy(bonAchatList = updatedBonAchatList) }
                            }
                        }

                    } catch (e: Exception) {
                        println("Error collecting BonAchat data: ${e.message}")
                        e.printStackTrace()
                    }
                }

                // Allow some time for initial data loading
                delay(100)

                // Enhanced products collection
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

                        // Continue monitoring repository changes
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
                        println("Error collecting Products data: ${e.message}")
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
        // Reset state before refreshing
        _uiState.update {
            it.copy(
                loadingProgress = 0f,
                isDataSyncing = true,
                hasStartedLoading = false,
                error = null,
                // Also reset data lists to ensure fresh loading
                bonAchatList = emptyList(),
                tariffsList = emptyList()
            )
        }

        // Clear the products list
        _uiState.value.produitInfosList.clear()

        loadTariffs()
    }

    // Enhanced sync status with more detailed information
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

    // Helper method to check if SQL operations are in progress
    fun isSqlSyncing(): Boolean = sqlRepository.isOperationInProgress()

    // Helper method to get detailed progress breakdown
    fun getProgressBreakdown(): Triple<Float, Float, Float> {
        val state = _uiState.value
        return Triple(state.sqlProgress, state.produitProgress, state.bonAchatProgress)
    }

    // Helper method to check if we should show loading indicator
    fun shouldShowLoadingIndicator(): Boolean {
        val state = _uiState.value
        return state.isDataSyncing ||
                (state.hasStartedLoading && state.loadingProgress < 1f) ||
                state.isInitializing
    }

    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        progressJob?.cancel()
    }
}
