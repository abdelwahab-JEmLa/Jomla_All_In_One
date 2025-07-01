package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ACentralFacade
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter= aCentral.getter

    fun getRelatedFAchatCouleurOperation(): (ArticlesBasesStatsTable, Int) -> FCouleurVentOperationInfos? {
        return aCentral.getter::getVentForArticleAndColorInThisApp
    }

}
