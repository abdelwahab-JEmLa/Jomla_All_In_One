package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingProgress = 0f) }

            val produitsdatabaseRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                ._2_1_ProduitsDataBase_Repository
            viewModelScope.launch {
                produitsdatabaseRepository.progressRepo.collect { progress ->
                    _uiState.update { it.copy(loadingProgress = progress) }
                }
            }

            viewModelScope.launch {
                try {
                    val repoList = produitsdatabaseRepository.modelDatasSnapList

                    // Initial update with current data
                    _uiState.update { currentState ->
                        // We need to create a new reference to the UI state's list
                        // and populate it with data from the repository
                        currentState.produitInfosList.clear()
                        currentState.produitInfosList.addAll(repoList)

                        currentState.copy(
                            bonAchatList = testBonAchatT2(),
                            tarificationList = testD_TarificationInfosT2(),
                            loadingProgress = 1f  // Set to 1 when complete
                        )
                    }

                    // Set up a snapshotFlow observer to listen for changes in the repository's list
                    // This approach works because we're using the same reference for produitInfosList
                    // Any changes in the repository's modelDatasSnapList will be reflected in our UI state

                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "Error loading tariffs",
                            loadingProgress = 0f  // Reset on error
                        )
                    }
                }
            }
        }
    }
}
