package V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment._01_VentsHistoriques.DataBase.Models

import V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment._01_VentsHistoriques.DataBase.Models._012_Vendeur.Companion.createVendeur
import V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment._01_VentsHistoriques.DataBase.Models._012_Vendeur.Companion.mapVendeurs
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class _01_VentsHistoriquesDataBase : RealmObject {
    var id: Long = 0L
    var dateDebutDeCettePeriode: String = "yyyy_MM_dd"
    var tempDebutDeCettePeriode: String = "HH:mm"

    @PrimaryKey
    var keyID: String = "${id}_${dateDebutDeCettePeriode.replace(".", "_")}_${tempDebutDeCettePeriode.replace(":", "_")}"

    var vendeurs: RealmList<_012_Vendeur> = realmListOf()

    companion object {
        // Enum to define database schema fields
        enum class NomsValeursModel {
            keyID,
            dateDebutDeCettePeriode,
            tempDebutDeCettePeriode,
            vendeurs
        }

        fun test_01_PeriodesVent(
            i: Int,
            testPeriodes: MutableList<_01_VentsHistoriquesDataBase>,
        ) {
            val date = "2025_04_${18 + i}"  // Using underscores instead of periods
            val time = "${10 + i}_00"       // Using underscores instead of colons
            val periodeKey = "${i}_${date}_${time}"  // Firebase-safe key

            val periode = _01_VentsHistoriquesDataBase().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                vendeurs = realmListOf()
            }

            for (j in 1..2) {
                val vendeurId = j.toLong()
                val vendeurNom = "_012_Vendeur $j"
                val vendeurKey = "$vendeurId->$vendeurNom"
                val vendeur = createVendeur(vendeurId, vendeurNom, vendeurKey)
                periode.vendeurs.add(vendeur)
            }

            testPeriodes.add(periode)
        }

        // Function to convert model to Firebase format
        fun convertToFirebaseFormat(periodes: List<_01_VentsHistoriquesDataBase>): Map<String, Any> {
            return periodes.associate { periode ->
                val validPeriodeKey = periode.keyID
                validPeriodeKey to mapOf(
                    NomsValeursModel.dateDebutDeCettePeriode.name to periode.dateDebutDeCettePeriode,
                    NomsValeursModel.tempDebutDeCettePeriode.name to periode.tempDebutDeCettePeriode,
                    NomsValeursModel.vendeurs.name to mapVendeurs(periode.vendeurs)
                )
            }
        }

        // Function to parse Firebase snapshot into model
        fun parsePeriodeFromSnapshot(snapshot: DataSnapshot): _01_VentsHistoriquesDataBase? {
            val periodeKey = snapshot.key ?: return null

            val date = snapshot.child(NomsValeursModel.dateDebutDeCettePeriode.name).getValue(String::class.java) ?: return null
            val time = snapshot.child(NomsValeursModel.tempDebutDeCettePeriode.name).getValue(String::class.java) ?: return null

            val periode = _01_VentsHistoriquesDataBase().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                vendeurs = realmListOf()
            }

            val vendeursSnapshot = snapshot.child(NomsValeursModel.vendeurs.name)
            vendeursSnapshot.children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach

                val vendeur = _012_Vendeur().apply {
                    keyID = vendeurKey
                    id = vendeurSnapshot.child("idVendeur").getValue(Long::class.java) ?: 0L
                    startDesignation = vendeurSnapshot.child("nomVendeur").getValue(String::class.java) ?: ""
                    acheteurs = realmListOf()
                }

                val acheteursSnapshot = vendeurSnapshot.child("_013_Acheteurs")
                acheteursSnapshot.children.forEach { acheteurSnapshot ->
                    val acheteur = _013_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot)
                        ?: return@forEach
                    vendeur.acheteurs.add(acheteur)
                }

                periode.vendeurs.add(vendeur)
            }

            return periode
        }
    }
}
