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
    var acheteurs: RealmList<_13_Acheteurs> = realmListOf()

    companion object{
        fun createVendeur(id: Long, nom: String, vendeurKey: String): _12_Vendeur {
            return _12_Vendeur().apply {
                keyID = vendeurKey
                idVendeur = id
                nomVendeur = nom
                acheteurs = realmListOf()

                // Create and add acheteurs
                val acheteursList = _13_Acheteurs.testData()
                acheteursList.forEach { acheteur ->
                    acheteurs.add(acheteur)
                }
            }
        }

        fun mapVendeurs(vendeurs: List<_12_Vendeur>): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "idVendeur" to vendeur.idVendeur,
                    "nomVendeur" to vendeur.nomVendeur,
                    "_13_Acheteurs" to _13_Acheteurs.mapDatas(vendeur.acheteurs)
                )
            }
        }
    }
}
