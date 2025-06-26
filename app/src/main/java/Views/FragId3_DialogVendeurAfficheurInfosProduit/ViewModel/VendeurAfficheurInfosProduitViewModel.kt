package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentral
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: ACentral,
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {

    fun getRelatedFAchatCouleurOperation(): (ArticlesBasesStatsTable, Int) -> FCouleurVentOperation? {
        return aCentral.getter::getVentForArticleAndColorInThisApp
    }

}
