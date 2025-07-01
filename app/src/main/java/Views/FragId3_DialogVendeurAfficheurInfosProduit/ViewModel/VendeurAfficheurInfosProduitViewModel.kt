package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter= aCentral.getter

    fun getRelatedFAchatCouleurOperation(): (ArticlesBasesStatsTable, Int) -> FCouleurVentOperationInfos? {
        return aCentral.getter::getVentForArticleAndColorInThisApp
    }

}
