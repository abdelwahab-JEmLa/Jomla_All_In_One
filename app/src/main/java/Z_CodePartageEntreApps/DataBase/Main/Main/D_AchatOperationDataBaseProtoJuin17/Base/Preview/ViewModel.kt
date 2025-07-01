package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class D_AchatOperationTestDatasViewModel(
    val a_CentralDatasHandlerProtoJuin9: AGetter,
) : ViewModel() {
      val mainData = a_CentralDatasHandlerProtoJuin9
          .fVentCouleurOperationRepository
          .datasValue

    data class UiStateSec9Frag1(val va: Int = 0)

    private val _uiState = MutableStateFlow(UiStateSec9Frag1())
    val uiState: StateFlow<UiStateSec9Frag1> = _uiState.asStateFlow()
}

