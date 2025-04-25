package V.DiviseParSections.App.SectionID5.Detailes.App.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.SectionID5.Detailes.App.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.createVendeur
import V.DiviseParSections.App.SectionID5.Detailes.App.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs.Companion.map_012_ComptsVendeurs
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

class _01_PeriodVentHistorique : RealmObject {

    @PrimaryKey
    var bsonObjectId: ObjectId = ObjectId()
    var tempCreationStr: String = getCurrentDataTimeString()

    var idPeriodDonAncienDataBase: Long = 0

    var fireBaseKeyID: String = "${idPeriodDonAncienDataBase}-($tempCreationStr)"

    // Section Etates Mutable


    var child_012_Compts_Vendeurs: RealmList<_012_ComptsVendeurs> = realmListOf()

    companion object {
        private const val TAG = "_01_VentsHistoriquesDB"

        // Schema constants - define field names to ensure consistency
        object SchemaFields {
            const val VID = "vid"
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val TEMP_CREATION = "tempCreationString"
            const val ID_PERIOD = "idPeriodDonAncienDataBase"
            const val FIREBASE_KEY_ID = "fireBaseKeyID"
            const val CHILD_VENDEURS = "child_012_Compts_Vendeurs"
        }

        fun parsePeriodeFromSnapshot(snapshot: DataSnapshot): _01_PeriodVentHistorique? {
            val periodeKey = snapshot.key ?: return null

            try {
                // Extract data from snapshot
                val objectIdStr = snapshot.child(SchemaFields.BSON_OBJECT_ID).getValue(String::class.java)
                val objectId = if (!objectIdStr.isNullOrEmpty()) {
                    try {
                        ObjectId(objectIdStr)
                    } catch (e: Exception) {
                        ObjectId()
                    }
                } else {
                    ObjectId()
                }

                val idPeriod = snapshot.child(SchemaFields.ID_PERIOD).getValue(Long::class.java) ?: 0L

                // Create and populate the periode object
                val periode = _01_PeriodVentHistorique().apply {
                    this.bsonObjectId = objectId
                    this.idPeriodDonAncienDataBase = idPeriod

                    val currentDateTime = getCurrentDataTimeString()
                    tempCreationStr = currentDateTime
                    fireBaseKeyID = periodeKey
                    child_012_Compts_Vendeurs = realmListOf()
                }

                // Parse vendeurs if they exist
                val vendeursSnapshot = snapshot.child(SchemaFields.CHILD_VENDEURS)
                vendeursSnapshot.children.forEach { vendeurSnapshot ->
                    val vendeur =
                        _012_ComptsVendeurs.parse_012_ComptsVendeursFromSnapshot(vendeurSnapshot)
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


        fun map_01_VentsHistoriquesDataBase(periodes: List<_01_PeriodVentHistorique>): Map<String, Any> {
            return periodes.associate { periode ->
                val validPeriodeKey = periode.fireBaseKeyID
                validPeriodeKey to mapOf(
                    SchemaFields.BSON_OBJECT_ID to periode.bsonObjectId.toString(),
                    SchemaFields.TEMP_CREATION to periode.tempCreationStr,
                    SchemaFields.ID_PERIOD to periode.idPeriodDonAncienDataBase,
                    SchemaFields.CHILD_VENDEURS to map_012_ComptsVendeurs(periode.child_012_Compts_Vendeurs)
                )
            }
        }

        fun getCurrentDataTimeString(): String =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) +
                    "(" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + ")"

        fun parse_fireBaseKeyID(value :Long=0): String {
            val getCurrentDataTimeString= getCurrentDataTimeString()

            return   "${value}-($getCurrentDataTimeString)"
        }

        /**
         * Generates test data for the _01_PeriodVentHistorique
         */
        fun test_01_PeriodesVent(
            i: Int,
            testPeriodes: MutableList<_01_PeriodVentHistorique>,
        ) {
            val date = "2025_04_${18 + i}"
            val time = "${10 + i}_00"       // Using underscores instead of colons
            val periodeKey = "${i}_${date}_${time}"  // Firebase-safe key

            val periode = _01_PeriodVentHistorique().apply {
                idPeriodDonAncienDataBase = i.toLong()
                fireBaseKeyID = periodeKey
                tempCreationStr = "$date-<$time"
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
         * Deep clones a _01_PeriodVentHistorique instance
         */
        fun deepCopy(source: _01_PeriodVentHistorique): _01_PeriodVentHistorique {
            return _01_PeriodVentHistorique().apply {
                bsonObjectId = source.bsonObjectId
                idPeriodDonAncienDataBase = source.idPeriodDonAncienDataBase
                fireBaseKeyID = source.fireBaseKeyID
                tempCreationStr = source.tempCreationStr

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
