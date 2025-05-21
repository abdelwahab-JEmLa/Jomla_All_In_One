package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.D_TarificationInfos
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
)

class TariffsButtonsViewModel_TestID2(
    val repo_0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys,
    val sqlRepository: InfosSqlDataBasesRepository,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null
    private var bonAchatCollectorJob: Job? = null

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()

        loadingJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingProgress = 0f) }

            try {
              //  val tariffsRepo = sqlRepository.modelListFlow

                val produitRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                    ._2_1_ProduitsDataBase_Repository

                val repoC3_BonAchat = repo_0_0_HeadSQLRepositorys
                    .repositorys_Model
                    .repository_1_3_TransactionCommercial

                val progressJob = launch {
                    combine(
                        produitRepository.progressRepo,
                        repoC3_BonAchat.progressRepo
                    ) { produitProgress, bonAchatProgress ->
                        (produitProgress + bonAchatProgress) / 2f
                    }.collect { combinedProgress ->
                        _uiState.update { it.copy(loadingProgress = combinedProgress) }
                    }
                }


                // Variable pour stocker la liste des tarifs
                var tariffsList = emptyList<D_TarificationInfos>()

                // Collect tariffs from tariffsRepo.d_TarificationInfos
              /*  launch {
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
                }      */

                bonAchatCollectorJob = launch {
                    val bonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()

                    _uiState.update { currentState ->
                        currentState.copy(
                            bonAchatList = bonAchatList,
                        )
                    }

                    launch {
                        repoC3_BonAchat.activeId.collect { _ ->
                            val updatedBonAchatList = repoC3_BonAchat.modelDatasSnapList.toList()
                            _uiState.update { it.copy(bonAchatList = updatedBonAchatList) }
                        }
                    }
                }

                delay(100)

                // Get products list from repository
                val repoSize = produitRepository.modelDatasSnapList.size

                if (repoSize > 0) {
                    val productsList = produitRepository.modelDatasSnapList.toList()

                    _uiState.update { currentState ->
                        currentState.produitInfosList.clear()
                        currentState.produitInfosList.addAll(productsList)
                        currentState.copy(loadingProgress = 1f)
                    }
                }

                progressJob.cancel()
                // Ne pas annuler le bonAchatCollectorJob car il doit continuer à collecter

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        loadingProgress = 0f
                    )
                }
            }
        }
    }

    fun refreshTariffs() {
        loadTariffs()
    }

    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
    }
}
