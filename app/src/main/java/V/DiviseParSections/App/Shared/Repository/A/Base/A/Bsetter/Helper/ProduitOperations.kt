
// File: RepositorysMainSetter/Helper/ProduitOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProduitOperations(
    private val bProduitInfosRepository: RepoM1ProduitInfos
) {
    val bproduitdatabaseSubclassfunctionality = bProduitInfosRepository

    fun deleteAddMultiDatas() {
        val datas = bproduitdatabaseSubclassfunctionality.datasValue
        CoroutineScope(Dispatchers.IO).launch {
            bproduitdatabaseSubclassfunctionality.dao.deleteAll()
            bproduitdatabaseSubclassfunctionality.dao.insertAll(datas)

            ArticlesBasesStatsTable.safeRemoveRef()
            bproduitdatabaseSubclassfunctionality.ancienRepo.batchFireBaseUpdateArticlesBasesStatsTable(
                datas
            )
        }
    }
}
