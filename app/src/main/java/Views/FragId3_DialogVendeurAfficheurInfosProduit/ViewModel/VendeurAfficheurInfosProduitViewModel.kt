package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val centralDatasHandler: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val d_AchatOperationComposeRepositoryPJ17=centralDatasHandler.d_AchatOperationComposeRepositoryPJ17
    fun update() {

    }
}
