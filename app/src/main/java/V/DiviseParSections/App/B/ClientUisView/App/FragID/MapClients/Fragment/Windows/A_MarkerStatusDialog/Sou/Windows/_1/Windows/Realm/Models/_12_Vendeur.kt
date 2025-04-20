package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _12_Vendeur : RealmObject {
    var idVendeur: Long = 0L
    var nomVendeur: String = ""

    @PrimaryKey
    var keyID: String = "_01_PeriodesVent.keyID-<{Ve}->(idVendeur=nomVendeur)"
    var produits: RealmList<_13_Produit> = realmListOf()

    companion object{
        fun createVendeur(id: Long, nom: String, vendeurKey: String): _12_Vendeur {
            return _12_Vendeur().apply {
                keyID = vendeurKey
                idVendeur = id
                nomVendeur = nom
                produits = realmListOf()

                for (k in 1..5) {
                    val produitId = k.toLong()
                    val produitNom = "_13_Produit $k"
                    val produitKey = "$keyID-<{Pr}->($produitId=$produitNom)"

                    val produit = _13_Produit.testproduit(produitKey, produitId, produitNom, k)

                    produits.add(produit)
                }
            }
        }

        fun mapVendeurs(vendeurs: List<_12_Vendeur>, periodeKey: String): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "idVendeur" to vendeur.idVendeur,
                    "nomVendeur" to vendeur.nomVendeur,
                    "produits" to _13_Produit.mapProduits(vendeur.produits, validVendeurKey)
                )
            }
        }
    }
}
