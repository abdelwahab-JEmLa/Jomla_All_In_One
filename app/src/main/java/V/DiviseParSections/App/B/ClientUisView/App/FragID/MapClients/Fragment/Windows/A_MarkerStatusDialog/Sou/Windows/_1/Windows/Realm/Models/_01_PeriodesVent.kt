package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._12_Vendeur.Companion.createVendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._12_Vendeur.Companion.mapVendeurs
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
                    "vendeurs" to mapVendeurs(periode.vendeurs, validPeriodeKey)
                )
            }
        }

    }
}

