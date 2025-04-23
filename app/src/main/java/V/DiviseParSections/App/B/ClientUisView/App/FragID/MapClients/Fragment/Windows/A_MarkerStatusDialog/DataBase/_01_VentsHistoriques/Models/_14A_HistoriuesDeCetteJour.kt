package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class _14A_HistoriuesDeCetteJour : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var dateCreationStr: String = getCurrentDateString()
    var tempCreationStr: String = getCurrentTimeString()

    var etateName: String = Etate.NON_DEFINI.name

    var description: String = ""

    @Ignore
    var etate: Etate
        get() = try {
            Etate.valueOf(etateName)
        } catch (e: Exception) {
            Etate.NON_DEFINI
        }
        set(value) {
            etateName = value.name
        }

    enum class Etate(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        COMMANDE_LENCE(android.R.color.holo_green_light, "في طلب"),       //<--
        //TODO(1): ajoute un verificateur si un achteur son dernier etate == COMMANDE_LENCE
        ACHAT_TERMINE(android.R.color.holo_purple,"تم البيع له" ),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق")
    }

    var fireBaseKeyID: String = "${etateName}->(${dateCreationStr}${tempCreationStr})"

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val DATE_CREATION = "dateCreationStr"
            const val TEMP_CREATION = "tempCreationStr"
            const val ETATE_NAME = "etateName"
            const val DESCRIPTION = "description"
        }

        fun mapDatas(datas: List<_14A_HistoriuesDeCetteJour>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.DATE_CREATION to data.dateCreationStr,
                    SchemaFields.TEMP_CREATION to data.tempCreationStr,
                    SchemaFields.ETATE_NAME to data.etateName,
                    SchemaFields.DESCRIPTION to data.description
                )
            }
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _14A_HistoriuesDeCetteJour? {
            val historiqueKey = snapshot.key ?: return null

            try {
                // Extract ObjectId if available, or create a new one
                val objectIdStr = snapshot.child(SchemaFields.BSON_OBJECT_ID).getValue(String::class.java)
                val objectId = if (!objectIdStr.isNullOrEmpty()) {
                    try {
                        ObjectId(objectIdStr)
                    } catch (e: Exception) {
                        BsonObjectId()
                    }
                } else {
                    BsonObjectId()
                }

                return _14A_HistoriuesDeCetteJour().apply {
                    fireBaseKeyID = historiqueKey
                    this.bsonObjectId = objectId
                    dateCreationStr = snapshot.child(SchemaFields.DATE_CREATION).getValue(String::class.java) ?: getCurrentDateString()
                    tempCreationStr = snapshot.child(SchemaFields.TEMP_CREATION).getValue(String::class.java) ?: getCurrentTimeString()
                    etateName = snapshot.child(SchemaFields.ETATE_NAME).getValue(String::class.java) ?: Etate.NON_DEFINI.name
                    description = snapshot.child(SchemaFields.DESCRIPTION).getValue(String::class.java) ?: ""
                }
            } catch (e: Exception) {
                return null
            }
        }

        fun deepCopy(source: _14A_HistoriuesDeCetteJour): _14A_HistoriuesDeCetteJour {
            return _14A_HistoriuesDeCetteJour().apply {
                this.bsonObjectId = source.bsonObjectId
                dateCreationStr = source.dateCreationStr
                tempCreationStr = source.tempCreationStr
                fireBaseKeyID = source.fireBaseKeyID
                etateName = source.etateName
                description = source.description
            }
        }

        fun getCurrentDateString(): String =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))

        fun getCurrentTimeString(): String =
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

}
