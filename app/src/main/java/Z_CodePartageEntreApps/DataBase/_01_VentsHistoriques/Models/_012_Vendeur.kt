package Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _012_Vendeur : RealmObject {
    var id: Long = 0L
    var startDesignation: String = ""

    @PrimaryKey
    var keyID: String = "${id}=${startDesignation.replace(" ", "_")}"

    var acheteurs: RealmList<_013_Acheteurs> = realmListOf()

    companion object{
        fun createVendeur(id: Long, nom: String, vendeurKey: String): _012_Vendeur {
            return _012_Vendeur().apply {
                keyID = vendeurKey
                this.id = id
                startDesignation = nom
                acheteurs = realmListOf()

                // Create and add acheteurs
                val acheteursList = _013_Acheteurs.testData()
                acheteursList.forEach { acheteur ->
                    acheteurs.add(acheteur)
                }
            }
        }

        fun mapVendeurs(vendeurs: List<_012_Vendeur>): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "idVendeur" to vendeur.id,
                    "nomVendeur" to vendeur.startDesignation,
                    "_013_Acheteurs" to _013_Acheteurs.mapDatas(vendeur.acheteurs)
                )
            }
        }
    }
}
