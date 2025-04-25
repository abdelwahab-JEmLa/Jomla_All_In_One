package Z_CodePartageEntreApps.Windows.B.Windows.ViewModel

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial._1_3_BonAchatRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial._1_3_BonAchat_Repository
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

data class UiState_StartUpScreen(
    var _1_1_CouleurAcheteOperationList: SnapshotStateList<_1_1_CouleurAcheteOperation> = mutableStateListOf(),
    var _1_2_ProduitAcheteOperationList: SnapshotStateList<_1_2_ProduitAcheteOperation> = mutableStateListOf(),
    var _1_3_TransactionCommercialList: SnapshotStateList<_1_3_TransactionCommercial> = mutableStateListOf(),
    var _1_4_PeriodeVentList: SnapshotStateList<_1_4_PeriodeVent> = mutableStateListOf(),

    var bonAchetOnCourseMntID: Long = 1,
    var isFilteringActive: Boolean = false,
    var errorMessage: String? = null,
    var syncInProgress: Boolean = false,
    var isDataLoading: Boolean = true,
    var isInitialized: Boolean = false,

    var itsManagerCompt: Boolean = false,
)

class ViewModelFragment_StartUpScreen(
    val _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository
) : ViewModel() {
    private val TAG = "ViewModelFragment_StartUpScreen"
    val headModel= _0_0_HeadOfRepositorys_Repository.repositorys_Model
    private val _uiState = MutableStateFlow(UiState_StartUpScreen())
    val uiStateFlow: StateFlow<UiState_StartUpScreen> = _uiState.asStateFlow()

    private val initializerViewModel = InintializeViewModel_StartUpScreen()

    init {
        loadData()

        viewModelScope.launch {
            initializerViewModel.waitForDataInitialization(
                _1_1_CouleurAcheteOperation_Repository,
                _1_2_ProduitAcheteOperation_Repository,
                _1_3_BonAchat_Repository,
                _1_4_PeriodeVent_Repository,
                _uiState,
                viewModelScope,
                { checkInitializationComplete() },
            )
        }


        // Add hardcoded data only if repository is empty
        viewModelScope.launch {
            if (_1_1_CouleurAcheteOperation_Repository.modelDatasSnapList.isEmpty()) {
                Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.A.AchatsManager.App._1.Shared.Test.addHardcodedDataToFirebase(
                    _1_1_CouleurAcheteOperation_Repository,
                    _1_2_ProduitAcheteOperation_Repository,
                    _1_3_BonAchat_Repository,
                    _1_4_PeriodeVent_Repository,
                )
            }
        }
    }
    private fun loadData() {
        _uiState.value = UiState_StartUpScreen(
            itsManagerCompt= headModel.activeIdDe_1_5_Vendeur > 0
        )
    }


    fun addData_1_4_PeriodeVent(newPeriodeVent: _1_4_PeriodeVent): Unit {
        viewModelScope.launch {
            _1_4_PeriodeVent_Repository.addData(newPeriodeVent)
        }
    }

    fun updateUneSeulData_1_4_PeriodeVent_Repository(data: _1_4_PeriodeVent) {
        viewModelScope.launch {
            _1_4_PeriodeVent_Repository.updateUnSeulData(data)
        }
    }

    private fun checkInitializationComplete() {
        initializerViewModel.checkInitializationComplete(
            _1_1_CouleurAcheteOperation_Repository,
            _1_2_ProduitAcheteOperation_Repository,
            _1_3_BonAchat_Repository,
            _1_4_PeriodeVent_Repository,
            _uiState,
        )
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
