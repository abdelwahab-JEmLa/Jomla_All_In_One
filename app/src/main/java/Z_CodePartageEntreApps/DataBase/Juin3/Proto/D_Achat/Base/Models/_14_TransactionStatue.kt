package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class _14_TransactionStatue : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var dateCreationStr: String = getCurrentDateString()
    var tempCreationStr: String = getCurrentTimeString()

    var etateTransactionName: String = EtateTransaction.NON_DEFINI.name

    var description: String = ""

    @Ignore
    var etateTransaction: EtateTransaction
        get() = try {
            EtateTransaction.valueOf(etateTransactionName)
        } catch (e: Exception) {
            EtateTransaction.NON_DEFINI
        }
        set(value) {
            etateTransactionName = value.name
        }

    enum class EtateTransaction(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        COMMANDE_LENCE(android.R.color.holo_green_light, "في طلب"),
        ACHAT_TERMINE(android.R.color.holo_purple,"تم البيع له" ),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق")
    }

    var fireBaseKeyID: String = "${etateTransactionName}->(${dateCreationStr}(${tempCreationStr}))"

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "key"
            const val DATE_CREATION = "dateCreationStr"
            const val TEMP_CREATION = "tempCreationStr"
            const val ETATE_NAME = "etateTransactionName"
            const val DESCRIPTION = "description"
        }

        fun mapDatas(datas: List<_14_TransactionStatue>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.DATE_CREATION to data.dateCreationStr,
                    SchemaFields.TEMP_CREATION to data.tempCreationStr,
                    SchemaFields.ETATE_NAME to data.etateTransactionName,
                    SchemaFields.DESCRIPTION to data.description
                )
            }
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _14_TransactionStatue? {
            val historiqueKey = snapshot.key ?: return null

            try {
                // Extract ObjectId if available, or create add new one
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

                return _14_TransactionStatue().apply {
                    fireBaseKeyID = historiqueKey
                    this.bsonObjectId = objectId
                    dateCreationStr = snapshot.child(SchemaFields.DATE_CREATION).getValue(String::class.java) ?: getCurrentDateString()
                    tempCreationStr = snapshot.child(SchemaFields.TEMP_CREATION).getValue(String::class.java) ?: getCurrentTimeString()
                    etateTransactionName = snapshot.child(SchemaFields.ETATE_NAME).getValue(String::class.java) ?: EtateTransaction.NON_DEFINI.name
                    description = snapshot.child(SchemaFields.DESCRIPTION).getValue(String::class.java) ?: ""
                }
            } catch (e: Exception) {
                return null
            }
        }

        fun deepCopy(source: _14_TransactionStatue): _14_TransactionStatue {
            return _14_TransactionStatue().apply {
                this.bsonObjectId = source.bsonObjectId
                dateCreationStr = source.dateCreationStr
                tempCreationStr = source.tempCreationStr
                fireBaseKeyID = source.fireBaseKeyID
                etateTransactionName = source.etateTransactionName
                description = source.description
            }
        }

        fun getCurrentDateString(): String =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))

        fun getCurrentTimeString(): String =
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

}
