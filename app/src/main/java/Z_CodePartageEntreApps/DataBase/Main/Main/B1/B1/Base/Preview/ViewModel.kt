package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class B1CouleurOuGoutProduitDataBaseTestDatasViewModel(
     val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
      val mainData = a_CentralDatasHandlerProtoJuin9
          .b1CouleurOuGoutProduitDataBase_Repository
          .datasValue

    data class UiState(val va: Int = 0)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}

