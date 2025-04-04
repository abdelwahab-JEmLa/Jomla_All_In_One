package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel

import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState_APP2_ID_2(
    var _1_1_CouleurAcheteOperationList: SnapshotStateList<_1_1_CouleurAcheteOperation> = mutableStateListOf(),
    var _1_2_ProduitAcheteOperationList: SnapshotStateList<_1_2_ProduitAcheteOperation> = mutableStateListOf(),
    var _1_3_BonAchatList: SnapshotStateList<_1_3_BonAchat> = mutableStateListOf(),
    var _1_4_PeriodeVentList: SnapshotStateList<_1_4_PeriodeVent> = mutableStateListOf(),

    var errorMessage: String? = null,
    var syncInProgress: Boolean = false,
    var isDataLoading: Boolean = true,
    var isInitialized: Boolean = false
)

class ViewModelFragment_APP2_ID_2(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_2"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_2())
    val uiStateFlow: StateFlow<UiState_APP2_ID_2> = _uiStateFlow.asStateFlow()

    private val initializerViewModel = InintializeViewModel_APP2_ID_2()

    init {
        viewModelScope.launch {
            initializerViewModel.waitForDataInitialization(
                _1_1_CouleurAcheteOperation_Repository,
                _1_2_ProduitAcheteOperation_Repository,
                _1_3_BonAchat_Repository,
                _1_4_PeriodeVent_Repository,
                _uiStateFlow,
                viewModelScope,
                { checkInitializationComplete() },
            )
        }

        // Add hardcoded data only if repository is empty
        viewModelScope.launch {
            if (_1_1_CouleurAcheteOperation_Repository.modelDatasSnapList.isEmpty()) {
                Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Test.addHardcodedDataToFirebase(
                    _1_1_CouleurAcheteOperation_Repository,
                    _1_2_ProduitAcheteOperation_Repository,
                    _1_3_BonAchat_Repository,
                    _1_4_PeriodeVent_Repository,
                )
            }
        }
    }

    private fun checkInitializationComplete() {
        initializerViewModel.checkInitializationComplete(
            _1_1_CouleurAcheteOperation_Repository,
            _1_2_ProduitAcheteOperation_Repository,
            _1_3_BonAchat_Repository,
            _1_4_PeriodeVent_Repository,
            _uiStateFlow,
        )
    }
    fun updateUneSeulData_1_4_PeriodeVent_Repository(data: _1_1_CouleurAcheteOperation) {
        viewModelScope.launch {
                _1_1_CouleurAcheteOperation_Repository.updateUnSeulData(data)
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
