package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.createVendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.mapVendeurs
import android.util.Log
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class _01_VentsHistoriquesDataBase : RealmObject {
    @PrimaryKey
    var keyID: String = "{vid}(tempCreationString)"

    var vid: ObjectId = ObjectId()

    var idPeriodDonAncienDataBase: Long = 0
    var dateDebutDeCettePeriode: String = getCurrentDataString()
    var tempDebutDeCettePeriode: String = getCurrentTimeString()

    var tempCreationString: String = "$dateDebutDeCettePeriode-<$tempDebutDeCettePeriode"

    var child_012_Compts_Vendeurs: RealmList<_012_ComptsVendeurs> = realmListOf()

    companion object {
        private const val TAG = "_01_VentsHistoriquesDB"

        // Schema constants - define field names to ensure consistency
        object SchemaFields {
            const val VID = "vid"
            const val DATE_DEBUT = "dateDebutDeCettePeriode"
            const val TEMP_DEBUT = "tempDebutDeCettePeriode"
            const val TEMP_CREATION = "tempCreationString"
            const val ID_PERIOD = "idPeriodDonAncienDataBase"
            const val CHILD_VENDEURS = "child_012_Compts_Vendeurs"
        }

        /**
         * Parses a Firebase DataSnapshot into a _01_VentsHistoriquesDataBase object
         * Updated to handle the new schema with ObjectId and proper structure
         */
        fun parsePeriodeFromSnapshot(snapshot: DataSnapshot): _01_VentsHistoriquesDataBase? {
            val periodeKey = snapshot.key ?: return null

            try {
                // Extract data from snapshot
                val vidValue = snapshot.child(SchemaFields.VID).getValue(String::class.java)
                val vid = if (vidValue != null) ObjectId(vidValue) else ObjectId()

                val date = snapshot.child(SchemaFields.DATE_DEBUT).getValue(String::class.java)
                    ?: return null
                val time = snapshot.child(SchemaFields.TEMP_DEBUT).getValue(String::class.java)
                    ?: return null
                val idPeriod = snapshot.child(SchemaFields.ID_PERIOD).getValue(Long::class.java) ?: 0L

                // Create and populate the periode object
                val periode = _01_VentsHistoriquesDataBase().apply {
                    this.vid = vid
                    this.idPeriodDonAncienDataBase = idPeriod
                    dateDebutDeCettePeriode = date
                    tempDebutDeCettePeriode = time
                    tempCreationString = "$date-<$time"
                    keyID = periodeKey
                    child_012_Compts_Vendeurs = realmListOf()
                }

                // Parse vendeurs if they exist
                val vendeursSnapshot = snapshot.child(SchemaFields.CHILD_VENDEURS)
                vendeursSnapshot.children.forEach { vendeurSnapshot ->
                    val vendeur = _012_ComptsVendeurs.parse_012_ComptsVendeursFromSnapshot(vendeurSnapshot)
                    if (vendeur != null) {
                        periode.child_012_Compts_Vendeurs.add(vendeur)
                    }
                }

                return periode
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing period from snapshot: ${e.message}", e)
                return null
            }
        }

        /**
         * Converts a list of _01_VentsHistoriquesDataBase objects to a Firebase-compatible format
         * Updated to handle ObjectId and new schema structure
         */
        fun convertToFirebaseFormat(periodes: List<_01_VentsHistoriquesDataBase>): Map<String, Any> {
            return periodes.associate { periode ->
                val validPeriodeKey = periode.keyID
                validPeriodeKey to mapOf(
                    SchemaFields.VID to periode.vid.toString(),
                    SchemaFields.DATE_DEBUT to periode.dateDebutDeCettePeriode,
                    SchemaFields.TEMP_DEBUT to periode.tempDebutDeCettePeriode,
                    SchemaFields.TEMP_CREATION to periode.tempCreationString,
                    SchemaFields.ID_PERIOD to periode.idPeriodDonAncienDataBase,
                    SchemaFields.CHILD_VENDEURS to mapVendeurs(periode.child_012_Compts_Vendeurs)
                )
            }
        }

        /**
         * Creates a new _01_VentsHistoriquesDataBase instance with the given parameters
         */
        fun createPeriode(
            id: Long,
            date: String = getCurrentDataString(),
            time: String = getCurrentTimeString()
        ): _01_VentsHistoriquesDataBase {
            val creationTimestamp = "$date-<$time"
            return _01_VentsHistoriquesDataBase().apply {
                idPeriodDonAncienDataBase = id
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                tempCreationString = creationTimestamp
                keyID = "${id}-($creationTimestamp)"
                child_012_Compts_Vendeurs = realmListOf()
            }
        }

        /**
         * Helper method to get the current date string in the format used by the application
         */
        fun getCurrentDataString(): String = LocalDate.now().format(
            DateTimeFormatter.ofPattern("yyyy_MM_dd")
        )

        /**
         * Helper method to get the current time string in the format used by the application
         */
        fun getCurrentTimeString(): String = LocalTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm")
        )

        /**
         * Generates test data for the _01_VentsHistoriquesDataBase
         */
        fun test_01_PeriodesVent(
            i: Int,
            testPeriodes: MutableList<_01_VentsHistoriquesDataBase>,
        ) {
            val date = "2025_04_${18 + i}"
            val time = "${10 + i}_00"       // Using underscores instead of colons
            val periodeKey = "${i}_${date}_${time}"  // Firebase-safe key

            val periode = _01_VentsHistoriquesDataBase().apply {
                idPeriodDonAncienDataBase = i.toLong()
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                tempCreationString = "$date-<$time"
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

        /**
         * Deep clones a _01_VentsHistoriquesDataBase instance
         */
        fun deepCopy(source: _01_VentsHistoriquesDataBase): _01_VentsHistoriquesDataBase {
            return _01_VentsHistoriquesDataBase().apply {
                vid = source.vid
                idPeriodDonAncienDataBase = source.idPeriodDonAncienDataBase
                keyID = source.keyID
                dateDebutDeCettePeriode = source.dateDebutDeCettePeriode
                tempDebutDeCettePeriode = source.tempDebutDeCettePeriode
                tempCreationString = source.tempCreationString

                // Deep copy vendors
                child_012_Compts_Vendeurs = realmListOf()
                source.child_012_Compts_Vendeurs.forEach { sourceVendeur ->
                    val vendeurCopy = _012_ComptsVendeurs.deepCopy(sourceVendeur)
                    child_012_Compts_Vendeurs.add(vendeurCopy)
                }
            }
        }
    }
}
