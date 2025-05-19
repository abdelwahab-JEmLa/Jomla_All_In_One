package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.util.Log
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
    private val TAG = "TariffsButtonsVM_ID2"

    // Track loading job to avoid race conditions
    private var loadingJob: Job? = null

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        // Cancel any existing job to prevent race conditions
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingProgress = 0f) }

            try {
                // Set up progress tracking
                val progressJob = launch {
                    val produitsdatabaseRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                        ._2_1_ProduitsDataBase_Repository

                    produitsdatabaseRepository.progressRepo.collect { progress ->
                        _uiState.update { it.copy(loadingProgress = progress) }
                    }
                }

                // Give a bit of time for the repository to initialize if needed
                delay(100)

                // Get the repository instance
                val produitsdatabaseRepository = repo_0_0_HeadSQLRepositorys.repositorys_Model
                    ._2_1_ProduitsDataBase_Repository

                // Check if repository has data
                val repoSize = produitsdatabaseRepository.modelDatasSnapList.size
                Log.d(TAG, "Repository product count: $repoSize")

                if (repoSize > 0) {
                    // Repository has data, update UI state
                    val productsList = produitsdatabaseRepository.modelDatasSnapList.toList()

                    _uiState.update { currentState ->
                        // Clear existing list
                        currentState.produitInfosList.clear()
                        // Add all products
                        currentState.produitInfosList.addAll(productsList)

                        // Log first few items for debugging
                        if (productsList.isNotEmpty()) {
                            val sampleSize = minOf(3, productsList.size)
                            Log.d(TAG, "Sample of loaded products (first $sampleSize):")
                            productsList.take(sampleSize).forEach { product ->
                                Log.d(TAG, "  Product ID: ${product.vid}, Name: ${product.nom}")
                            }
                        }

                        currentState.copy(loadingProgress = 1f)
                    }
                }

                // Cancel progress tracking once loading is complete
                progressJob.cancel()

                Log.d(TAG, "Tariffs loading completed - produitInfosList size: ${_uiState.value.produitInfosList.size}")
                Log.d(TAG, "Tariffs loading completed - bonAchatList size: ${_uiState.value.bonAchatList.size}")
                Log.d(TAG, "Tariffs loading completed - tarificationList size: ${_uiState.value.tarificationList.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tariffs: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        loadingProgress = 0f
                    )
                }
            }
        }
    }

    // Function to force reload tariffs data
    fun refreshTariffs() {
        loadTariffs()
    }
}
