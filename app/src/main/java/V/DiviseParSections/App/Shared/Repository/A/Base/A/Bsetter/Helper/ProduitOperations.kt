
// File: RepositorysMainSetter/Helper/ProduitOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProduitOperations(
    private val bProduitInfosRepository: RepoM1Produit
) {
    val bproduitdatabaseSubclassfunctionality = bProduitInfosRepository

    fun deleteAddMultiDatas(list_M1Produit: List<M01Produit>) {
        CoroutineScope(Dispatchers.IO).launch {
            bproduitdatabaseSubclassfunctionality.dao.deleteAll()
            bproduitdatabaseSubclassfunctionality.dao.insertAll(list_M1Produit)

            M01Produit.safe_Remove_DataBase_Ref()
            bproduitdatabaseSubclassfunctionality.dataBaseCreationFactory.batchFireBaseUpdateArticlesBasesStatsTable(
                list_M1Produit
            )
        }
    }
}
