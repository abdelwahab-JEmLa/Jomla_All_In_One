package W.Fragments.A.PanierFinaleDAchat.APP.ViewModel

import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(
    var _1_1_CouleurAcheteOperationList: SnapshotStateList<_1_1_CouleurAcheteOperation> = mutableStateListOf(),
    var _1_2_ProduitAcheteOperationList: SnapshotStateList<_1_2_ProduitAcheteOperation> = mutableStateListOf(),
    var _1_3_BonAchatList: SnapshotStateList<_1_3_BonAchat> = mutableStateListOf(),
    var _1_4_PeriodeVentList: SnapshotStateList<_1_4_PeriodeVent> = mutableStateListOf(),

    var isFilteringActive: Boolean = false,
    var errorMessage: String? = null,
    var syncInProgress: Boolean = false,
    var isDataLoading: Boolean = true,
    var isInitialized: Boolean = false
)

class ViewModelFragment_APP2_FragID_1(
    val _1_1_CouleurAcheteOperationRepository: _1_1_CouleurAcheteOperationRepository,
    val _1_2_ProduitAcheteOperationRepository: _1_2_ProduitAcheteOperationRepository,
    val _1_3_BonAchatRepository: _1_3_BonAchatRepository,
    val _1_4_PeriodeVentRepository: _1_4_PeriodeVentRepository
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_FragID_1" // Tag for logging

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    private val initializerViewModel = InintializeViewModel()

    init {
        viewModelScope.launch {
            initializerViewModel.waitForDataInitialization(
                _1_1_CouleurAcheteOperationRepository,
                _1_2_ProduitAcheteOperationRepository,
                _1_3_BonAchatRepository,
                _1_4_PeriodeVentRepository,
                _uiStateFlow,
                viewModelScope,
                { checkInitializationComplete() },
            )
        }

        // Add hardcoded data only if repository is empty
        viewModelScope.launch {
            if (_1_1_CouleurAcheteOperationRepository.modelDatasSnapList.isEmpty()) {
                Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Test.addHardcodedDataToFirebase(
                    _1_1_CouleurAcheteOperationRepository,
                    _1_2_ProduitAcheteOperationRepository,
                    _1_3_BonAchatRepository,
                    _1_4_PeriodeVentRepository,
                )
            }
        }
    }

    fun updateUneSeulData_1_1_CouleurAcheteOperation(data: _1_1_CouleurAcheteOperation) {
        viewModelScope.launch {
            try {
                _1_1_CouleurAcheteOperationRepository.updateUnSeulData(data)
                updateFilteredListIfNeeded()
            } catch (e: Exception) {
                handleError("Error updating data", e)
            }
        }
    }

    fun updateUneSeulData_1_2_ProduitAcheteOperation(data: _1_2_ProduitAcheteOperation) {
        viewModelScope.launch {
            try {
                _1_2_ProduitAcheteOperationRepository.updateUnSeulData(data)
                // No need to update filtered list as we're not displaying this in UI directly
                Log.d(TAG, "Updated _1_2_ProduitAcheteOperation with ID: ${data.vid}")
            } catch (e: Exception) {
                handleError("Error updating _1_2_ProduitAcheteOperation data", e)
            }
        }
    }

    fun updateUneSeulData_1_3_BonAchat(data: _1_3_BonAchat) {
        viewModelScope.launch {
            try {
                _1_3_BonAchatRepository.updateUnSeulData(data)
                // No need to update filtered list as we're not displaying this in UI directly
                Log.d(TAG, "Updated _1_3_BonAchat with ID: ${data.id}")
            } catch (e: Exception) {
                handleError("Error updating _1_3_BonAchat data", e)
            }
        }
    }

    fun updateUneSeulData_1_4_PeriodeVent(data: _1_4_PeriodeVent) {
        viewModelScope.launch {
            try {
                _1_4_PeriodeVentRepository.updateUnSeulData(data)
                // No need to update filtered list as we're not displaying this in UI directly
                Log.d(TAG, "Updated _1_4_PeriodeVent with ID: ${data.id}")
            } catch (e: Exception) {
                handleError("Error updating _1_4_PeriodeVent data", e)
            }
        }
    }

    private fun checkInitializationComplete() {
        initializerViewModel.checkInitializationComplete(
            _1_1_CouleurAcheteOperationRepository,
            _1_2_ProduitAcheteOperationRepository,
            _1_3_BonAchatRepository,
            _1_4_PeriodeVentRepository,
            _uiStateFlow,
            { updateFilteredListIfNeeded() }
        )
    }

    private fun updateFilteredListIfNeeded() {
        if (_uiStateFlow.value.isFilteringActive) {
            filterByLastPeriode()
        } else {
            resetFilter()
        }
    }

    fun filterByLastPeriode() {
        viewModelScope.launch {
            try {
                // Use repository's initialDataLoaded flag instead of local ensureDataIsInitialized
                val repoImpl = _1_1_CouleurAcheteOperationRepository as? _1_1_CouleurAcheteOperationRepositoryImpl
                if (repoImpl != null && !repoImpl.initialDataLoaded) {
                    // Wait until data is initialized
                    withContext(Dispatchers.IO) {
                        while (!repoImpl.initialDataLoaded) {
                            kotlinx.coroutines.delay(100)
                        }
                    }
                }

                // Get the latest period
                val latestPeriode = _1_4_PeriodeVentRepository.modelDatasSnapList.maxByOrNull { it.id }
                if (latestPeriode != null) {
                    // Update the UI state to indicate filtering is active
                    _uiStateFlow.value = _uiStateFlow.value.copy(
                        isFilteringActive = true
                    )

                    Log.d(TAG, "Applied last period filter for period ID: ${latestPeriode.id}")
                } else {
                    Log.d(TAG, "No periods found to filter by")
                    resetFilter()
                }
            } catch (e: Exception) {
                handleError("Error applying filter", e)
            }
        }
    }

    fun resetFilter() {
        viewModelScope.launch {
            try {
                // Use repository's initialDataLoaded flag instead of local ensureDataIsInitialized
                val repoImpl = _1_1_CouleurAcheteOperationRepository as? _1_1_CouleurAcheteOperationRepositoryImpl
                if (repoImpl != null && !repoImpl.initialDataLoaded) {
                    // Wait until data is initialized
                    withContext(Dispatchers.IO) {
                        while (!repoImpl.initialDataLoaded) {
                            kotlinx.coroutines.delay(100)
                        }
                    }
                }

                // Update the UI state to indicate filtering is inactive
                _uiStateFlow.value = _uiStateFlow.value.copy(
                    isFilteringActive = false
                )

                Log.d(TAG, "Filter reset, showing all items")
            } catch (e: Exception) {
                handleError("Error resetting filter", e)
            }
        }
    }

    private fun handleError(message: String, exception: Exception) {
        Log.e(TAG, "$message: ${exception.message}")
        _uiStateFlow.value = _uiStateFlow.value.copy(
            errorMessage = "$message: ${exception.message}"
        )
    }

    override fun onCleared() {
        super.onCleared()
        val repoImpl1 = _1_1_CouleurAcheteOperationRepository as? _1_1_CouleurAcheteOperationRepositoryImpl
        repoImpl1?.cleanup()

        val repoImpl2 = _1_2_ProduitAcheteOperationRepository as? _1_2_ProduitAcheteOperationRepositoryImpl
        repoImpl2?.cleanup()

        val repoImpl3 = _1_3_BonAchatRepository as? _1_3_BonAchatRepositoryImpl
        repoImpl3?.cleanup()

        val repoImpl4 = _1_4_PeriodeVentRepository as? _1_4_PeriodeVentRepositoryImpl
        repoImpl4?.cleanup()
    }
}
