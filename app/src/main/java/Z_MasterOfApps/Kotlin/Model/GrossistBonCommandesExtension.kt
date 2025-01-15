package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import android.util.Log

open class GrossistBonCommandesExtension {
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
