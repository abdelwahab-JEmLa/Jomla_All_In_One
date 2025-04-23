package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._14_

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour.Etate
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.mongodb.kbson.BsonObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class _14A_HistoriuesDeCetteJour_RepositoryImpl() : _14A_HistoriuesDeCetteJour_Repository {
    private val TAG = "_14A_Historique_Repo"

    private val realm: Realm = createRealm()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    object SchemaFields {
        const val BSON_OBJECT_ID = "bsonObjectId"
        const val DATE_CREATION = "dateCreationStr"
        const val TEMP_CREATION = "tempCreationStr"
        const val ETATE_NAME = "etateName"
        const val DESCRIPTION = "description"
    }

    override fun mapDatas(datas: List<_14A_HistoriuesDeCetteJour>): Map<String, Any> {
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

    override fun parseDataFromSnapshot(snapshot: DataSnapshot): _14A_HistoriuesDeCetteJour? {
        val historiqueKey = snapshot.key ?: return null

        try {
            // Extract ObjectId if available, or create a new one
            val objectIdStr = snapshot.child(SchemaFields.BSON_OBJECT_ID).getValue(String::class.java)
            val objectId = if (!objectIdStr.isNullOrEmpty()) {
                try {
                    BsonObjectId(objectIdStr)
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

    override fun deepCopy(source: _14A_HistoriuesDeCetteJour): _14A_HistoriuesDeCetteJour {
        return _14A_HistoriuesDeCetteJour().apply {
            this.bsonObjectId = source.bsonObjectId
            dateCreationStr = source.dateCreationStr
            tempCreationStr = source.tempCreationStr
            fireBaseKeyID = source.fireBaseKeyID
            etateName = source.etateName
            description = source.description
        }
    }

    override fun testData(): List<_14A_HistoriuesDeCetteJour> {
        val data = mutableListOf<_14A_HistoriuesDeCetteJour>()

        for (k in 1..3) {
            val historique = _14A_HistoriuesDeCetteJour().apply {
                dateCreationStr = "2025_04_${20 + k}"
                tempCreationStr = "12:${k*10}"
                etateName = Etate.entries[(k % Etate.entries.size)].name
                description = "Historique d'état $k"
                fireBaseKeyID = "${etateName}->(${dateCreationStr}${tempCreationStr})"
            }

            data.add(historique)
        }

        return data
    }

    private fun createRealm(): Realm {
        val config = RealmConfiguration.create(
            schema = setOf(_14A_HistoriuesDeCetteJour::class)
        )
        return Realm.open(config)
    }

    override fun getHistoriquesByDate(dateStr: String): List<_14A_HistoriuesDeCetteJour> {
        return try {
            realm.query<_14A_HistoriuesDeCetteJour>("dateCreationStr == $0", dateStr)
                .find()
                .toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getHistoriquesByEtate(etatName: String): List<_14A_HistoriuesDeCetteJour> {
        return try {
            realm.query<_14A_HistoriuesDeCetteJour>("etateName == $0", etatName)
                .find()
                .toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Helper methods to fix the reference to _01_VentsHistoriquesDataBase_Repository static methods
    private fun getCurrentDateString(): String =
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))

    private fun getCurrentTimeString(): String =
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

}
