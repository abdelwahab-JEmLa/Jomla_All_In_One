package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log

open class GrossistBonCommandesExtension {
    fun updateSelf(
        produit: _ModelAppsFather.ProduitModel,
        bonCommande: _ModelAppsFather.ProduitModel.GrossistBonCommandes,
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

    // In GrossistBonCommandes.kt
    fun calculeSelf(product: _ModelAppsFather.ProduitModel, viewModelInitApp: ViewModelInitApp) {
        Log.d("CalculeSelf", "Starting calculeSelf for product ${product.id}")
        viewModelInitApp._modelAppsFather.produitsMainDataBase
            .filter { it.id == product.id }
            .forEach { produit ->
                try {

                    val newBonCommande = _ModelAppsFather.ProduitModel.GrossistBonCommandes().apply {
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

                        // Create a temporary list to hold the processed colors
                        val processedColors = mutableListOf<_ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>()


                        produit.bonsVentDeCetteCota
                            .flatMap { it.colours_Achete }
                            .groupBy { it.couleurId }
                            .forEach { (couleurId, colorList) ->

                                colorList.firstOrNull()?.let { firstColor ->
                                    val totalQuantity = colorList.sumOf { it.quantity_Achete }

                                    val newCommendee = _ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                        id = couleurId,
                                        nom = firstColor.nom,
                                        emoji = firstColor.imogi
                                    ).apply {
                                        quantityAchete = totalQuantity
                                    }

                                    if (newCommendee.quantityAchete > 0) {
                                        processedColors.add(newCommendee)
                                    }
                                }
                            }

                        coloursEtGoutsCommendee.addAll(processedColors)
                    }

                    produit.bonCommendDeCetteCota = newBonCommande

                    updateChildren(newBonCommande, produit)

                } catch (e: Exception) {
                    Log.e("CalculeSelf", "Calculation error for product ${produit.id}", e)
                    Log.e("CalculeSelf", "Stack trace: ${e.stackTraceToString()}")
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
                "coloursEtGoutsCommendeeList" to newBonCommande.coloursEtGoutsCommendee,
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
