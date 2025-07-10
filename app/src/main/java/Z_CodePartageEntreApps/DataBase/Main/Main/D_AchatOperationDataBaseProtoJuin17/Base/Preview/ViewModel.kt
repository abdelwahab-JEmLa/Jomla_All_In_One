package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class D_AchatOperationTestDatasViewModel(
    val a_CentralDatasHandlerProtoJuin9: RepositorysMainGetter,
) : ViewModel() {
      val mainData = a_CentralDatasHandlerProtoJuin9
          .repo10OperationVentCouleur
          .datasValue

    data class UiStateSec9Frag1(val va: Int = 0)

    private val _uiState = MutableStateFlow(UiStateSec9Frag1())
    val uiState: StateFlow<UiStateSec9Frag1> = _uiState.asStateFlow()
}

