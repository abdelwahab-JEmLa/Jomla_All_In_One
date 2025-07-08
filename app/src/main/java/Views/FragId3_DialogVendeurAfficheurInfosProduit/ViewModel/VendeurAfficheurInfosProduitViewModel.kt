package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.CentralFacade
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: CentralFacade,
) : ViewModel() {
    val getter= aCentral.get

    fun getRelatedFAchatCouleurOperation(): (ArticlesBasesStatsTable, Int) -> M10OperationVentCouleur? {
        return aCentral.get::getVentForArticleAndColorInThisApp
    }

}
