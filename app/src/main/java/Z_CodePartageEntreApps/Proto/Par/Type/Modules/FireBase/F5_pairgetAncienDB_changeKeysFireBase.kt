package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import Z_CodePartageEntreApps.Model.A_ProduitInfos
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

suspend fun F0_FireBaseOperationsHandler.extractedFrom_getAncienDB_changeKeysFireBase(
        refDBJetPackExport: DatabaseReference
    ): Pair<Int, Map<String, A_ProduitInfos>> {
        onProgressUpdate(0.3f)
        val articlesSnapshot = refDBJetPackExport.get().await()

        val articles = articlesSnapshot.children.mapNotNull { snapshot ->
            try {
                snapshot.getValue(ArticlesBasesStatsTable::class.java)
            } catch (e: Exception) {
                println("Error parsing article: ${e.message}")
                null
            }
        }

        onProgressUpdate(0.5f)
        val productsWithoutKeys = articles.map { product ->
            product.copy(keyFireBase = "")
        }

        val a_ProduitInfosList = convertArticlesBasesToProduitInfos(productsWithoutKeys)
        val originalCount = a_ProduitInfosList.size


        onProgressUpdate(0.7f)
        val resultMap = setListDataInlineFun<A_ProduitInfos>(a_ProduitInfosList)

        onProgressUpdate(1f)

        return Pair(originalCount, resultMap)
    }
