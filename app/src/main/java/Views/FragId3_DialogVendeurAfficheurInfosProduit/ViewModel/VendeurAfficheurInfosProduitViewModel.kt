package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter= aCentral.repositorysMainGetter

    fun getRelatedFAchatCouleurOperation(): (M01Produit, Int) -> M10OperationVentCouleur? {
        return aCentral.repositorysMainGetter::getVentForArticleAndColorInThisApp
    }

}
