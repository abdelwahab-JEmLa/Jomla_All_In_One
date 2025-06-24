package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId

class Sec10Frag1ViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {

    data class UiState(val catalogueFilterId: BsonObjectId? = null)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun acheter(
        produit: ArticlesBasesStatsTable,
        baseFileName: String,
        colorIndex: Int,
        quantity: Int,
    ) {
        val data = aCentralDatasHandlerProtoJuin9
            .zAppComptRepositoryComposable
            .ouvrireProduitEtCouleurVent(produit, baseFileName)

        data.let {
            aCentralDatasHandlerProtoJuin9
                .dCouleurAchatOperationRepositoryComposable
                .acheterUneCouleur(it, produit, quantity, colorIndex)
        }
    }
}
