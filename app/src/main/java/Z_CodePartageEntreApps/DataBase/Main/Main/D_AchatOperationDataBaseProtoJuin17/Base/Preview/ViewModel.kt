package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.Preview

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.AGetterCentral
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class D_AchatOperationTestDatasViewModel(
    val a_CentralDatasHandlerProtoJuin9: AGetterCentral,
) : ViewModel() {
      val mainData = a_CentralDatasHandlerProtoJuin9
          .fCouleurAchatOperationRepositoryComposable
          .datasValue

    data class UiStateSec9Frag1(val va: Int = 0)

    private val _uiState = MutableStateFlow(UiStateSec9Frag1())
    val uiState: StateFlow<UiStateSec9Frag1> = _uiState.asStateFlow()
}

