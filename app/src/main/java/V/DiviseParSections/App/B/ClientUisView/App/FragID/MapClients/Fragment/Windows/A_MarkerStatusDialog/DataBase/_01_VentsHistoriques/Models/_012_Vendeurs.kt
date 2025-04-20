package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _012_Vendeurs : RealmObject {
    var id: Long = 0L
    var startDesignation: String = ""

    @PrimaryKey
    var keyID: String = "${id}=${startDesignation.replace(" ", "_")}"

    var child_013_Acheteurs: RealmList<_013_Acheteurs> = realmListOf()

    companion object{
        fun createVendeur(id: Long, nom: String, vendeurKey: String): _012_Vendeurs {
            return _012_Vendeurs().apply {
                keyID = vendeurKey
                this.id = id
                startDesignation = nom
                child_013_Acheteurs = realmListOf()

                // Create and add acheteurs
                val acheteursList = _013_Acheteurs.testData()
                acheteursList.forEach { acheteur ->
                    child_013_Acheteurs.add(acheteur)
                }
            }
        }

        fun mapVendeurs(vendeurs: List<_012_Vendeurs>): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "idVendeur" to vendeur.id,
                    "nomVendeur" to vendeur.startDesignation,
                    "_013_Acheteurs" to _013_Acheteurs.mapDatas(vendeur.child_013_Acheteurs)
                )
            }
        }
    }
}
