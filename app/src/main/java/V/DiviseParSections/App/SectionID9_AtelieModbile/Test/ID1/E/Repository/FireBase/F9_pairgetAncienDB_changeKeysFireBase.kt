package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

suspend fun F0_FireBaseOperationsHandler.extracteFrom_getAncienDB_changeKeysFireBase(
        refDBJetPackExport: DatabaseReference
    ): Pair<Int, Map<String, A_ProduitInfos>> {
        onProgressUpdate(0.3f)
        val articlesSnapshot = refDBJetPackExport.get().await()
    F6_FirebaseDebugUtils.logFirebaseOperation(
        "getAncienDB_ARTICLES_FETCH",
        refDBJetPackExport,
        articlesSnapshot.childrenCount.toInt(),
        articlesSnapshot.exists()
    )

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

    F6_FirebaseDebugUtils.logFirebaseOperation(
        "getAncienDB_CONVERSION",
        ref,
        originalCount,
        true
    )

        onProgressUpdate(0.7f)
        val resultMap = setDataInlineFun<A_ProduitInfos>(a_ProduitInfosList)

        onProgressUpdate(1f)
    F6_FirebaseDebugUtils.logFirebaseOperation(
        "getAncienDB_SUCCESS",
        ref,
        resultMap.size,
        true
    )
        return Pair(originalCount, resultMap)
    }
