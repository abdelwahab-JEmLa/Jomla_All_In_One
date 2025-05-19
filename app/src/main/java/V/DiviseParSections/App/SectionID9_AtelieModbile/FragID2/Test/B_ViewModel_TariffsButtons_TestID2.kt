package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
data class UiState(
    var bonAchatList: List<BonAchatT2> = emptyList(),
    var produitInfosList: List<ArticlesBasesStatsTable> = emptyList(),
    var tarificationList: List<D_TarificationInfosT2> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isInitialSetupComplete: Boolean = false,
)
class TariffsButtonsViewModel_TestID2(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val tariffs = testD_TarificationInfosT2()
                val produitInfos = testDataArticlesBasesStatsTable2()
                val bonAchat = testBonAchatT2()

                _uiState.update {
                    it.copy(
                        bonAchatList = bonAchat,
                        produitInfosList = produitInfos,
                        tarificationList = tariffs,
                        isLoading = false,
                        isInitialSetupComplete = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        isLoading = false
                    )
                }
            }
        }
    }
}
