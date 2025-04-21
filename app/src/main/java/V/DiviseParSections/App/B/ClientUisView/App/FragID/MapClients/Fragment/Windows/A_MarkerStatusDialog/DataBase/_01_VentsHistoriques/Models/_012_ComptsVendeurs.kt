package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _012_ComptsVendeurs : RealmObject {
    var vid: Long = 0L
    var idCompt: Long = 0L
    var startDesignation: String = "_012_ComptsVendeurs $idCompt"

    @PrimaryKey
    var keyID: String = "${vid}=${startDesignation}"

    var child_013_Acheteurs: RealmList<_013_Acheteurs> = realmListOf()

    companion object{
        fun mapVendeurs(vendeurs: List<_012_ComptsVendeurs>): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "vid" to vendeur.vid,
                    "idCompt" to vendeur.idCompt,
                    "startDesignation" to vendeur.startDesignation,
                    "child_013_Acheteurs" to _013_Acheteurs.mapDatas(vendeur.child_013_Acheteurs)
                )
            }
        }

        fun parse_012_ComptsVendeursFromSnapshot(snapshot: DataSnapshot): _012_ComptsVendeurs? {
            val vendeurKey = snapshot.key ?: return null

            val vendeur = _012_ComptsVendeurs().apply {
                keyID = vendeurKey
                vid = snapshot.child("vid").getValue(Long::class.java) ?: 0L
                idCompt = snapshot.child("idCompt").getValue(Long::class.java) ?: 0L
                startDesignation = snapshot.child("startDesignation").getValue(String::class.java) ?: ""
                child_013_Acheteurs = realmListOf()
            }

            val acheteursSnapshot = snapshot.child("child_013_Acheteurs")
            acheteursSnapshot.children.forEach { acheteurSnapshot ->
                val acheteur = _013_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot)
                    ?: return@forEach
                vendeur.child_013_Acheteurs.add(acheteur)
            }

            return vendeur
        }

        fun createVendeur(id: Long, nom: String, vendeurKey: String): _012_ComptsVendeurs {
            return _012_ComptsVendeurs().apply {
                keyID = vendeurKey
                this.vid = id
                startDesignation = nom
                child_013_Acheteurs = realmListOf()

                // Create and add acheteurs
                val acheteursList = _013_Acheteurs.testData()
                acheteursList.forEach { acheteur ->
                    child_013_Acheteurs.add(acheteur)
                }
            }
        }
        fun deepCopy(source: _012_ComptsVendeurs): _012_ComptsVendeurs {
            return _012_ComptsVendeurs().apply {
                vid = source.vid
                idCompt = source.idCompt
                startDesignation = source.startDesignation
                keyID = source.keyID

                // Deep copy acheteurs
                child_013_Acheteurs = realmListOf()
                source.child_013_Acheteurs.forEach { sourceAcheteur ->
                    child_013_Acheteurs.add(_013_Acheteurs.deepCopy(sourceAcheteur))
                }
            }
        }

    }
}
