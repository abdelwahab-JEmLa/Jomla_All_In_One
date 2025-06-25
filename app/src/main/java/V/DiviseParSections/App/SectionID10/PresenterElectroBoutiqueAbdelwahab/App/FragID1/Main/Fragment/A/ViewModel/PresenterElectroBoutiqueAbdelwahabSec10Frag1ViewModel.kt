package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId

class PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {

    data class UiState(val catalogueFilterId: BsonObjectId? = null)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun acheter(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val data = aCentralDatasHandlerProtoJuin9
            .zAppComptRepositoryComposable
            .ouvrireProduitEtCouleurVent(produit, colorIndex)
        data.let {
            aCentralDatasHandlerProtoJuin9
                .fCouleurAchatOperationRepositoryComposable
                .acheterUneCouleur(it, produit, quantity, colorIndex)
        }
    }
}
