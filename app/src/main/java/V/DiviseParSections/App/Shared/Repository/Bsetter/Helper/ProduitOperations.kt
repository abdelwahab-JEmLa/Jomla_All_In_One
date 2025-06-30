
// File: BSetter/Helper/ProduitOperations.kt
package V.DiviseParSections.App.Shared.Repository.Bsetter.Helper

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProduitOperations(
    private val bProduitInfosRepository: BProduitInfosRepository
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
