package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _13_Produit : RealmObject {
    var idProduit: Long = 0L
    var nomProduit: String = ""

    @PrimaryKey
    var keyID: String = "_12_Vendeur.keyID-<{Pr}->(idProduit=nomProduit)"
    var quantity: Int = 0

    companion object{

        fun testproduit(
            produitKey: String,
            produitId: Long,
            produitNom: String,
            k: Int,
        ): _13_Produit {
            val produit = _13_Produit().apply {
                keyID = produitKey
                idProduit = produitId
                nomProduit = produitNom
                quantity = (k * 5)
            }
            return produit
        }

         fun mapProduits(produits: List<_13_Produit>, vendeurKey: String): Map<String, Any> {
            return produits.associate { produit ->
                val validProduitKey = produit.keyID

                validProduitKey to mapOf(
                    "idProduit" to produit.idProduit,
                    "nomProduit" to produit.nomProduit,
                    "quantity" to produit.quantity
                )
            }
        }
        // Add this to the companion object in _13_Produit.kt
        fun parseProduitFromSnapshot(snapshot: DataSnapshot): _13_Produit? {
            val produitKey = snapshot.key ?: return null
            if (!produitKey.contains("<{Pr}->")) return null

            return _13_Produit().apply {
                keyID = produitKey
                idProduit = snapshot.child("idProduit").getValue(Long::class.java) ?: 0L
                nomProduit = snapshot.child("nomProduit").getValue(String::class.java) ?: ""
                quantity = snapshot.child("quantity").getValue(Int::class.java) ?: 0
            }
        }


    }
}
