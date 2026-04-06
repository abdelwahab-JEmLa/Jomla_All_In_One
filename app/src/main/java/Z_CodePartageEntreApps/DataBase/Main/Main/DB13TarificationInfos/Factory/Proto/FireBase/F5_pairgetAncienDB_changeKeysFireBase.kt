package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Proto.FireBase

import Z_CodePartageEntreApps.Model.A_ProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

suspend fun F0_FireBaseOperationsHandler.extractedFrom_getAncienDB_changeKeysFireBase(
        refDBJetPackExport: DatabaseReference
    ): Pair<Int, Map<String, A_ProduitInfos>> {
        onProgressUpdate(0.3f)
        val articlesSnapshot = refDBJetPackExport.get().await()

        val articles = articlesSnapshot.children.mapNotNull { snapshot ->
            try {
                snapshot.getValue(M01Produit::class.java)
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
