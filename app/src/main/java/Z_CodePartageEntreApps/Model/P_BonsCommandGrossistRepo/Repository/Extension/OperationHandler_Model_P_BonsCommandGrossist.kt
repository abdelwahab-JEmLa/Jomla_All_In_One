package Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository.Extension

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model.P_BonsCommandGrossist
import androidx.compose.runtime.snapshots.SnapshotStateList

object OperationHandler_Model_P_BonsCommandGrossist {
    private const val TAG = "OperationHandler_P_BonsCommandGrossist"

    fun groupedProduitsParGrossist(soldArticlesTabelle: SnapshotStateList<SoldArticlesTabelle>)
            : List<P_BonsCommandGrossist> {
        if (soldArticlesTabelle.isEmpty()) {
            return emptyList()
        }

        val allProductsGrouped = soldArticlesTabelle
            .groupBy { it.idArticle }
            .map { (productId, articles) ->
                val firstArticle = articles.first()

                // Extract colors with quantity > 0
                val couleursEtGouts = articles.flatMap { article ->
                    article.colorsAcheterIdsToQuantity.map { (colorId, quantity) ->
                        CouleurETGout(
                            id = colorId,
                            sumQuantityDEsAchats = quantity.toLong()
                        )
                    }
                }

                A_ProduitModel(
                    id = productId,
                    init_nom = firstArticle.nameArticle,
                    // Map CouleurETGout to ColourEtGout_Model
                    init_colours_Et_Gouts = couleursEtGouts.map { couleurEtGout ->
                        A_ProduitModel.ColourEtGout_Model(
                            id = couleurEtGout.id,
                            position_Du_Couleur_Au_Produit = couleurEtGout.id
                        )
                    }
                )
            }

        // Now group the products by the actual grossist ID from the data
        val productsGroupedByGrossist = allProductsGrouped
            .groupBy { 0L }

        // Always find the maximum vid in existing models to generate next ID
        val maxVid = soldArticlesTabelle.maxOfOrNull { it.vid } ?: 0L

        // Create GrossistGroup objects for each grossist
        return productsGroupedByGrossist.entries.mapIndexed { index, entry ->
            val grossistId = entry.key
            val products = entry.value

            // Create new unique ID by finding max + 1 for each new item
            val newVid = maxVid + index + 1

            P_BonsCommandGrossist(
                vid = newVid
            ).apply {
                infosDeBase.GrossistChoisiID = grossistId
                produitCommendeIDs = products.map { it.id }
            }
        }
    }
}
data class CouleurETGout(
    val id: Long = 0,
    val sumQuantityDEsAchats: Long = 0,
)
