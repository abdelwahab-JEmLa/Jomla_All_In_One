package W.Fragments.A.PanierFinaleDAchat.APP.ViewModel

import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState_APP2_ID_1(
    var _1_1_CouleurAcheteOperationList: SnapshotStateList<_1_1_CouleurAcheteOperation> = mutableStateListOf(),
    var _1_2_ProduitAcheteOperationList: SnapshotStateList<_1_2_ProduitAcheteOperation> = mutableStateListOf(),
    var _1_3_BonAchatList: SnapshotStateList<_1_3_BonAchat> = mutableStateListOf(),
    var _1_4_PeriodeVentList: SnapshotStateList<_1_4_PeriodeVent> = mutableStateListOf(),

    var bonAchetOnCourseMntID: Long = 1,
    var isFilteringActive: Boolean = false,
    var errorMessage: String? = null,
    var syncInProgress: Boolean = false,
    var isDataLoading: Boolean = true,
    var isInitialized: Boolean = false
)

class ViewModelFragment_APP2_ID_1(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_2"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_1())
    val uiStateFlow: StateFlow<UiState_APP2_ID_1> = _uiStateFlow.asStateFlow()

    private val initializerViewModel = InintializeViewModel_APP2_ID_1()

    init {
        viewModelScope.launch {
            initializerViewModel.waitForDataInitialization(
                _1_1_CouleurAcheteOperation_Repository,
                _1_2_ProduitAcheteOperation_Repository,
                _1_3_BonAchat_Repository,
                _1_4_PeriodeVent_Repository,
                _uiStateFlow,
                viewModelScope,
                {
                    checkInitializationComplete(
                        _1_1_CouleurAcheteOperation_Repository,
                        _1_2_ProduitAcheteOperation_Repository,
                        _1_3_BonAchat_Repository,
                        _1_4_PeriodeVent_Repository,
                        _uiStateFlow
                    )
                }
            )
        }
    }

    fun addData_1_3_BonAchat_Repository(newData: _1_3_BonAchat): Unit {
        _1_3_BonAchat_Repository.addData(newData)
    }

    // Add this to checkInitializationComplete method in InintializeViewModel_APP2_ID_1
    fun checkInitializationComplete(
        _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
        _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
        _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
        _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository,
        uiStateFlow: MutableStateFlow<UiState_APP2_ID_1>,
    ) {
        val progress1 = _1_1_CouleurAcheteOperation_Repository.progressRepo.value
        val progress2 = _1_2_ProduitAcheteOperation_Repository.progressRepo.value
        val progress3 = _1_3_BonAchat_Repository.progressRepo.value
        val progress4 = _1_4_PeriodeVent_Repository.progressRepo.value

        Log.d(TAG, "Progress values: $progress1, $progress2, $progress3, $progress4, isInitialized: ${uiStateFlow.value.isInitialized}")

        if (progress1 >= 1.0f && progress2 >= 1.0f && progress3 >= 1.0f && progress4 >= 1.0f) {
            if (!uiStateFlow.value.isInitialized) {
                Log.d(TAG, "Setting initialization complete")
                uiStateFlow.value = uiStateFlow.value.copy(
                    isInitialized = true
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        val repoImpl1 =
            _1_1_CouleurAcheteOperation_Repository as? _1_1_CouleurAcheteOperationRepositoryImpl
        repoImpl1?.cleanup()

        val repoImpl2 =
            _1_2_ProduitAcheteOperation_Repository as? _1_2_ProduitAcheteOperationRepositoryImpl
        repoImpl2?.cleanup()

        val repoImpl3 = _1_3_BonAchat_Repository as? _1_3_BonAchatRepositoryImpl
        repoImpl3?.cleanup()

        val repoImpl4 = _1_4_PeriodeVent_Repository as? _1_4_PeriodeVentRepositoryImpl
        repoImpl4?.cleanup()
    }
}
