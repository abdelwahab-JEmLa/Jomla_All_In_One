package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentral
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId

class PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel(
     aCentral: ACentral,
) : ViewModel() {
    val getter = aCentral.getter
    val setter = aCentral.setter

    data class UiState(val catalogueFilterId: BsonObjectId? = null)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun acheterACaSetterCentralProto26(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        setter.acheterACaSetterCentral(
            vent,
            relatedCouleurKeyParAncienMethod,
            produit,
            colorIndex,
            quantity
        )
    }
}
