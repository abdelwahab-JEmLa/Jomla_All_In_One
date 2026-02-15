package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import android.util.Log

// Fixed: Return the updated data instead of void, and made synchronous
fun getData_AvecUpdated_Carton(
    oldDatas: List<OldDataBase_M1>,
    aCentralFacade: ACentralFacade
): List<ArticlesBasesStatsTable> {
    return try {
        val updatedProducts = mutableListOf<ArticlesBasesStatsTable>()

        // Wait for the repository to have data loaded
        val currentProducts = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue

        if (currentProducts.isEmpty()) {
            Log.w("getData_AvecUpdated_Carton", "No current products found in repository")
            return emptyList()
        }

        oldDatas.forEach { old ->
            val m1Produit_IN_New = currentProducts.find { it.id == old.id }

            if (m1Produit_IN_New != null) {
                val updatedProduct = m1Produit_IN_New.copy(
                    quantite_Boit_Par_Carton = old.nmbrCaron
                )
                updatedProducts.add(updatedProduct)
                Log.d(
                    "getData_AvecUpdated_Carton",
                    "Updated product ID: ${old.id} with carton quantity: ${old.nmbrCaron}"
                )
            } else {
                Log.w(
                    "getData_AvecUpdated_Carton",
                    "Product with ID ${old.id} not found in current products"
                )
            }
        }

        Log.i(
            "getData_AvecUpdated_Carton",
            "Successfully processed ${updatedProducts.size} products out of ${oldDatas.size} old data items"
        )
        updatedProducts
    } catch (e: Exception) {
        Log.e("getData_AvecUpdated_Carton", "Error updating carton data: ${e.message}", e)
        emptyList()
    }
}
