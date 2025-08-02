
// File: RepositorysMainSetter/Helper/ProduitOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProduitOperations(
    private val bProduitInfosRepository: RepoM1Produit
) {
    val bproduitdatabaseSubclassfunctionality = bProduitInfosRepository

    fun deleteAddMultiDatas(list_M1Produit: List<ArticlesBasesStatsTable>) {
        CoroutineScope(Dispatchers.IO).launch {
            bproduitdatabaseSubclassfunctionality.dao.deleteAll()
            bproduitdatabaseSubclassfunctionality.dao.insertAll(list_M1Produit)

            ArticlesBasesStatsTable.safe_Remove_DataBase_Ref()
            bproduitdatabaseSubclassfunctionality.dataBaseCreationFactory.batchFireBaseUpdateArticlesBasesStatsTable(
                list_M1Produit
            )
        }
    }
}
