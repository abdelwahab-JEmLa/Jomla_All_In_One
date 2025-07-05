package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter= aCentral.getter

    fun getRelatedFAchatCouleurOperation(): (ArticlesBasesStatsTable, Int) -> M10OperationVentCouleur? {
        return aCentral.getter::getVentForArticleAndColorInThisApp
    }

}
