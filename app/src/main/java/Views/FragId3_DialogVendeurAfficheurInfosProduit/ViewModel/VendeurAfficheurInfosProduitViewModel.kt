package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
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
