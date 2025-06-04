package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState_APP2_ID_2(
    var errorMessage: String? = null,
    var syncInProgress: Boolean = false,
    var isDataLoading: Boolean = true,
    var isInitialized: Boolean = false
)

class ViewModelFragment_APP2_ID_2(
    val _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_2"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_2())
    val uiStateFlow: StateFlow<UiState_APP2_ID_2> = _uiStateFlow.asStateFlow()


}
