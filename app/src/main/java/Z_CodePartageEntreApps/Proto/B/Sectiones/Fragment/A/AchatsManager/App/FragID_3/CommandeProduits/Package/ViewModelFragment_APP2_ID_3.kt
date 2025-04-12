package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState_APP2_ID_3(
    var _4_CouleurOperationCommand: SnapshotStateList<_4_CouleurOperationCommand> = mutableStateListOf(),
    var errorMessage: String? = null,
    var isDataLoading: Boolean = true,
)

class ViewModelFragment_APP2_ID_3(
    val _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_3"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_3())
    val uiStateFlow: StateFlow<UiState_APP2_ID_3> = _uiStateFlow.asStateFlow()

    val repositorys_Model = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    private val a_1_1_CouleurAcheteOperation =
        repositorys_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList

    private val b_1_2_ProduitAcheteOperation =
        repositorys_Model._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList

    val _2_1_ProduitsDataBase =
        repositorys_Model._2_1_ProduitsDataBase_Repository.modelDatasSnapList

}
