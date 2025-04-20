package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._12_Vendeur.Companion.createVendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._12_Vendeur.Companion.mapVendeurs
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class _01_PeriodesVent : RealmObject {
    var dateDebutDeCettePeriode: String = "yyyy.MM.dd"
    var tempDebutDeCettePeriode: String = "HH:mm"

    @PrimaryKey
    var keyID: String =  "{PV}->dateDebutDeCettePeriode=HH:mm"

    var vendeurs: RealmList<_12_Vendeur> = realmListOf()
    companion object {
        fun test_01_PeriodesVent(
            i: Int,
            testPeriodes: MutableList<_01_PeriodesVent>,
        ) {
            val date = "2025_04_${18 + i}"
            val time = "${10 + i}:00"
            val periodeKey = "{PV}->$date=$time"

            val periode = _01_PeriodesVent().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                vendeurs = realmListOf()
            }

            for (j in 1..2) {
                val vendeurId = j.toLong()
                val vendeurNom = "_12_Vendeur $j"
                val vendeurKey = "$periodeKey-<{Ve}->($vendeurId=$vendeurNom)"
                val vendeur = createVendeur(vendeurId, vendeurNom, vendeurKey)
                periode.vendeurs.add(vendeur)
            }

            testPeriodes.add(periode)
        }
        fun convertToFirebaseFormat(periodes: List<_01_PeriodesVent>): Map<String, Any> {
            return periodes.associate { periode ->
                val validPeriodeKey = periode.keyID
                validPeriodeKey to mapOf(
                    "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                    "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                    "vendeurs" to mapVendeurs(periode.vendeurs)
                )
            }
        }
        // Add this to the companion object in _01_PeriodesVent.kt
        fun parsePeriodeFromSnapshot(snapshot: DataSnapshot): _01_PeriodesVent? {
            val periodeKey = snapshot.key ?: return null
            if (!periodeKey.startsWith("{PV}->")) return null

            val date = snapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: return null
            val time = snapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: return null

            val periode = _01_PeriodesVent().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                vendeurs = realmListOf()
            }

            val vendeursSnapshot = snapshot.child("vendeurs")
            vendeursSnapshot.children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach
                if (!vendeurKey.contains("<{Ve}->")) return@forEach

                val vendeur = _12_Vendeur().apply {
                    keyID = vendeurKey
                    idVendeur = vendeurSnapshot.child("idVendeur").getValue(Long::class.java) ?: 0L
                    nomVendeur = vendeurSnapshot.child("nomVendeur").getValue(String::class.java) ?: ""
                    acheteurs = realmListOf()
                }

                val acheteursSnapshot = vendeurSnapshot.child("acheteurs")
                acheteursSnapshot.children.forEach { acheteurSnapshot ->
                    val acheteur = _13_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot) ?: return@forEach
                    vendeur.acheteurs.add(acheteur)
                }

                periode.vendeurs.add(vendeur)
            }

            return periode
        }

    }
}

