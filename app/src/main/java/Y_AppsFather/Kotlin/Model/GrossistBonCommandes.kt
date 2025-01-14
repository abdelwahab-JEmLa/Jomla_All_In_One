package Y_AppsFather.Kotlin.Model

import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee
import Y_AppsFather.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log

open class ExtensionGrossistBonCommandes {
    fun updateSelf(
        produit: _ModelAppsFather.ProduitModel,
        bonCommande: GrossistBonCommandes,
        viewModelProduits: ViewModelInitApp
    ) {
        produit.bonCommendDeCetteCota = bonCommande
        val index =
            viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == produit.id }
        if (index != -1) {
            // Direct update of the SnapshotStateList
            viewModelProduits._modelAppsFather.produitsMainDataBase[index] = produit
        }
    }

    fun calculeSelf(product: _ModelAppsFather.ProduitModel, viewModelInitApp: ViewModelInitApp) {
        viewModelInitApp._modelAppsFather.produitsMainDataBase
            .filter { it.id == product.id }
            .forEach { produit ->
                try {
                    val newBonCommande = GrossistBonCommandes().apply {
                        vid = System.currentTimeMillis()
                        grossistInformations = _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations(
                            id = vid,
                            nom = "Non Defini",
                            couleur = "#FF0000"
                        ).apply {
                            auFilterFAB = false
                            positionInGrossistsList = 0
                        }

                        // Initialize empty list for Firebase
                        coloursEtGoutsCommendee.clear()

                        Log.d("CalculeSelf", "Started processing for product ${produit.id}")

                        // Create a temporary list to hold the processed colors
                        val processedColors =
                            mutableListOf<_ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>()

                        produit.bonsVentDeCetteCota
                            .flatMap { it.colours_Achete }
                            .groupBy { it.couleurId }
                            .forEach { (couleurId, colorList) ->
                                Log.d("CalculeSelf", "Processing color: $couleurId")

                                colorList.firstOrNull()?.let { firstColor ->
                                    val totalQuantity = colorList.sumOf { it.quantity_Achete }

                                    val newCommendee = ColoursGoutsCommendee(
                                        id = couleurId,
                                        nom = firstColor.nom,
                                        emoji = firstColor.imogi
                                    ).apply {
                                        quantityAchete = totalQuantity
                                    }

                                    if (newCommendee.quantityAchete > 0) {
                                        processedColors.add(newCommendee)
                                        Log.d(
                                            "CalculeSelf",
                                            "Added to processed colors: $couleurId, quantity: $totalQuantity"
                                        )
                                    }
                                }
                            }

                        // Add all processed colors to the commendee list
                        coloursEtGoutsCommendee.addAll(processedColors)

                        Log.d(
                            "CalculeSelf",
                            "Final coloursEtGoutsCommendee size: ${coloursEtGoutsCommendee.size}"
                        )
                    }

                    produit.bonCommendDeCetteCota = newBonCommande

                    updateChildren(newBonCommande, produit)

                } catch (e: Exception) {
                    Log.e("CalculeSelf", "Calculation error for product ${produit.id}", e)
                    e.printStackTrace()
                }
            }
    }

    fun updateChildren(
        newBonCommande: _ModelAppsFather.ProduitModel.GrossistBonCommandes,
        produit: _ModelAppsFather.ProduitModel
    ) {
        // Create a map for Firebase update
        val updates = mapOf(
            "bonCommendDeCetteCota" to mapOf(
                "vid" to newBonCommande.vid,
                "grossistInformations" to newBonCommande.grossistInformations,
                "coloursEtGoutsCommendeeList" to newBonCommande.coloursEtGoutsCommendee.map { commendee ->
                    mapOf(
                        "id" to commendee.id,
                        "nom" to commendee.nom,
                        "emoji" to commendee.emoji,
                        "quantityAchete" to commendee.quantityAchete
                    )
                },
                "date" to newBonCommande.date,
                "date_String_Divise" to newBonCommande.date_String_Divise,
                "time_String_Divise" to newBonCommande.time_String_Divise,
                "currentCreditBalance" to newBonCommande.currentCreditBalance,
                "cpositionCheyCeGrossit" to newBonCommande.cPositionCheyCeGrossit,
                "positionProduitDonGrossistChoisiPourAcheterCeProduit" to
                        newBonCommande.positionProduitDonGrossistChoisiPourAcheterCeProduit
            )
        )

        // Update Firebase with explicit structure
        produitsFireBaseRef.child(produit.id.toString())
            .updateChildren(updates)
            .addOnSuccessListener {
                Log.d(
                    "CalculeSelf",
                    "Successfully updated Firebase for product ${produit.id}"
                )
            }
            .addOnFailureListener { exception ->
                Log.e("CalculeSelf", "Firebase update failed", exception)
            }
    }
}
