package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.Preview

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.AvantJuin17.Proto.A_CentralDatasHandlerProtoJuin9
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(val va: Int = 0)

class D_AchatOperationTestDatasViewModel(
    private val a_CentralDatasHandlerProtoJuin9: A_CentralDatasHandlerProtoJuin9,
) : ViewModel() {
    val appComptComposeRepository = a_CentralDatasHandlerProtoJuin9.appComptComposeRepository
    val achatOperationComposeRepository = a_CentralDatasHandlerProtoJuin9.achatOperationComposeRepository

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
