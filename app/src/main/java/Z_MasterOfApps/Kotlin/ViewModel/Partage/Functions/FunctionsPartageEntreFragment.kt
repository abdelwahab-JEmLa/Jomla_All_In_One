package Z_MasterOfApps.Kotlin.ViewModel.Partage.Functions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.Firebase
import com.google.firebase.database.database

class FunctionsPartageEntreFragment(
    val viewModelInitApp: ViewModelInitApp,
) {
    fun changeColours_AcheteQuantity_Achete(
        selectedBonVent: ProduitModel.ClientBonVentModel?,
        produit: ProduitModel,
        color: ProduitModel.ClientBonVentModel.ColorAchatModel,
        newQuantity: Int
    ) {
        // Update the product first
        val updatedProduit = produit.apply {
            bonsVentDeCetteCota.find { it == selectedBonVent }
                ?.let { bonVent ->
                    bonVent.colours_Achete.find { it == color }
                        ?.quantity_Achete = newQuantity
                }
        }
        updateProduit(updatedProduit, viewModelInitApp)

        // Update SoldArticlesTabelle
        selectedBonVent?.let { bonVent ->
            val soldArticlesRef = Firebase.database.getReference("O_SoldArticlesTabelle")

            // Create a query to find the existing record
            soldArticlesRef.orderByChild("idArticle").equalTo(produit.id.toDouble())
                .get()
                .addOnSuccessListener { snapshot ->
                    val existingRecord = snapshot.children.find {
                        it.child("clientSoldToItId").getValue(Long::class.java) == bonVent.clientIdChoisi
                    }

                    val sortedColors = bonVent.colours_Achete
                        .filter { it.quantity_Achete > 0 }
                        .sortedBy { it.vidPosition }
                        .take(4)  // Only take first 4 colors as per SoldArticlesTabelle structure

                    val updates = mutableMapOf<String, Any>()

                    // Build the record
                    val record = mapOf(
                        "idArticle" to produit.id,
                        "nameArticle" to produit.nom,
                        "clientSoldToItId" to bonVent.clientIdChoisi,
                        "date" to java.time.LocalDate.now().toString(),
                        "confimed" to false
                    ) + sortedColors.withIndex().flatMap { (index, colorModel) ->
                        listOf(
                            "color${index + 1}IdPicked" to colorModel.couleurId,
                            "color${index + 1}SoldQuantity" to colorModel.quantity_Achete
                        )
                    }.toMap()

                    if (existingRecord != null) {
                        // Update existing record
                        updates["/${existingRecord.key}"] = record
                    } else {
                        // Create new record
                        val newRecordRef = soldArticlesRef.push()
                        updates["/${newRecordRef.key}"] = record
                    }

                    // Perform the update
                    soldArticlesRef.updateChildren(updates)
                        .addOnFailureListener { exception ->
                            // Handle any errors
                            android.util.Log.e("SoldArticles", "Error updating sold articles", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    android.util.Log.e("SoldArticles", "Error querying sold articles", exception)
                }
        }
    }
}
