package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.createVendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.mapVendeurs
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class _01_VentsHistoriquesDataBase : RealmObject {
    var id: Long = 0L
    var dateDebutDeCettePeriode: String = getCurrentDataString()
    var tempDebutDeCettePeriode: String = getCurrentTimeString()

    private var tempCreationString: String = "$dateDebutDeCettePeriode-<$tempDebutDeCettePeriode"

    @PrimaryKey
    var keyID: String = "${id}($tempCreationString)"

    var child_012_Compts_Vendeurs: RealmList<_012_ComptsVendeurs> = realmListOf()

    companion object {

        fun getCurrentDataString(): String = LocalDate.now().format(
            DateTimeFormatter.ofPattern("yyyy_MM_dd")
        )

        fun getCurrentTimeString(): String = LocalTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm")
        )

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
            val date = "2025_04_${18 + i}"
            val time = "${10 + i}_00"       // Using underscores instead of colons
            val periodeKey = "${i}_${date}_${time}"  // Firebase-safe key

            val periode = _01_VentsHistoriquesDataBase().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                child_012_Compts_Vendeurs = realmListOf()
            }

            for (j in 1..2) {
                val vendeurId = j.toLong()
                val vendeurNom = "_012_ComptsVendeurs $j"
                val vendeurKey = "$vendeurId->$vendeurNom"
                val vendeur = createVendeur(vendeurId, vendeurNom, vendeurKey)
                periode.child_012_Compts_Vendeurs.add(vendeur)
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
                    NomsValeursModel.vendeurs.name to mapVendeurs(periode.child_012_Compts_Vendeurs)
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
                child_012_Compts_Vendeurs = realmListOf()
            }

            val vendeursSnapshot = snapshot.child(NomsValeursModel.vendeurs.name)
            vendeursSnapshot.children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach

                val vendeur = _012_ComptsVendeurs().apply {
                    keyID = vendeurKey
                    vid = vendeurSnapshot.child("idVendeur").getValue(Long::class.java) ?: 0L
                    startDesignation = vendeurSnapshot.child("nomVendeur").getValue(String::class.java) ?: ""
                    child_013_Acheteurs = realmListOf()
                }

                val acheteursSnapshot = vendeurSnapshot.child("_013_Acheteurs")
                acheteursSnapshot.children.forEach { acheteurSnapshot ->
                    val acheteur = _013_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot)
                        ?: return@forEach
                    vendeur.child_013_Acheteurs.add(acheteur)
                }

                periode.child_012_Compts_Vendeurs.add(vendeur)
            }

            return periode
        }
    }
}
