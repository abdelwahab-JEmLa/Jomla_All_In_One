package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ZViewModel_Sec1Frag3(
    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val a_ProduitDataBaseComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.bProduitDataBase_SubClassFunctionality
    val d_AchatOperationComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.fCouleurAchatOperationRepositoryComposable

    data class UiState_Sec1Frag3(
        val v: String = "",
    )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()
}
