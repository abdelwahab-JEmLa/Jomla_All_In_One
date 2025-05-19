package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    var produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase> = mutableStateListOf(),
    var bonAchatList: List<BonAchatT2> = emptyList(),
    var tarificationList: List<D_TarificationInfosT2> = emptyList(),
    val loadingProgress: Float = 0f,
    val error: String? = null,
)

class TariffsButtonsViewModel_TestID2(
    private val appDatabase: AppDatabase,
    val repo_0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingProgress = 0f) }

            try {
                val progressJob = launch {
                    val produitsDataBaseRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                        ._2_1_ProduitsDataBase_Repository

                    produitsDataBaseRepository.progressRepo.collect { progress ->
                        _uiState.update { it.copy(loadingProgress = progress) }
                    }
                }

                val testBonAchat = testBonAchatT2()
                val tarificationList = testD_TarificationInfosT2()

                _uiState.update { currentState ->
                    currentState.copy(
                        tarificationList = tarificationList,
                        bonAchatList = testBonAchat,
                    )
                }

                delay(100)

                val produitsdatabaseRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                    ._2_1_ProduitsDataBase_Repository

                val repoSize = produitsdatabaseRepository.modelDatasSnapList.size

                if (repoSize > 0) {
                    val productsList = produitsdatabaseRepository.modelDatasSnapList.toList()

                    _uiState.update { currentState ->
                        currentState.produitInfosList.clear()
                        currentState.produitInfosList.addAll(productsList)
                        currentState.copy(loadingProgress = 1f)
                    }
                }

                progressJob.cancel()

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
}
