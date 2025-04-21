package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.getCurrentDataTimeString
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _013_Acheteurs : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var idClient: Long = 0L

    var startDesignation: String = ""
    var tempDateCreationStr: String = getCurrentDataTimeString()

    var fireBaseKeyID: String = "${this.idClient}=${this.tempDateCreationStr}"

    var child_14Produits: RealmList<_014_Produits> = realmListOf()


    var etateValue: String = Etate.NON_DEFINI.name

    // Create a transient property with getter/setter for the enum
    @Ignore
    var etate: Etate
        get() = try {
            Etate.valueOf(etateValue)
        } catch (e: Exception) {
            Etate.NON_DEFINI
        }
        set(value) {
            etateValue = value.name
        }

    enum class Etate(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        COMMANDE_LENCE(android.R.color.holo_green_light, "نشط / متصل"),
        CLIENT_ABSENT(android.R.color.darker_gray, "غائب الشاري"),
        FERME(android.R.color.darker_gray, "مغلق")
    }

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val FIREBASE_KEY_ID = "fireBaseKeyID"
            const val ID_CLIENT = "idClient"
            const val START_DESIGNATION = "startDesignation"
            const val TEMP_DATE_CREATION = "tempDateCreationStr"
            const val CHILD_PRODUITS = "child_14Produits"
            const val ETATE_VALUE = "etateValue"
        }

        fun mapDatas(datas: List<_013_Acheteurs>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.ID_CLIENT to data.idClient,
                    SchemaFields.START_DESIGNATION to data.startDesignation,
                    SchemaFields.TEMP_DATE_CREATION to data.tempDateCreationStr,
                    SchemaFields.CHILD_PRODUITS to _014_Produits.mapDatas(data.child_14Produits),
                    SchemaFields.ETATE_VALUE to data.etateValue
                )
            }
        }

        fun parse_13_AcheteursFromSnapshot(snapshot: DataSnapshot): _013_Acheteurs? {
            val acheteurKey = snapshot.key ?: return null

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

                val acheteur = _013_Acheteurs().apply {
                    fireBaseKeyID = acheteurKey
                    this.bsonObjectId = objectId
                    idClient = snapshot.child(SchemaFields.ID_CLIENT).getValue(Long::class.java) ?: 0L
                    startDesignation = snapshot.child(SchemaFields.START_DESIGNATION).getValue(String::class.java) ?: ""
                    tempDateCreationStr = snapshot.child(SchemaFields.TEMP_DATE_CREATION).getValue(String::class.java) ?: "yyyy.mm.dd(HH:mm)"
                    child_14Produits = realmListOf()
                    etateValue = snapshot.child(SchemaFields.ETATE_VALUE).getValue(String::class.java) ?: Etate.NON_DEFINI.name
                }

                val produitsSnapshot = snapshot.child(SchemaFields.CHILD_PRODUITS)
                produitsSnapshot.children.forEach { produitSnapshot ->
                    val produit = _014_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                    acheteur.child_14Produits.add(produit)
                }

                return acheteur
            } catch (e: Exception) {
                return null
            }
        }

        fun deepCopy(source: _013_Acheteurs): _013_Acheteurs {
            return _013_Acheteurs().apply {
                this.bsonObjectId = source.bsonObjectId
                idClient = source.idClient
                startDesignation = source.startDesignation
                tempDateCreationStr = source.tempDateCreationStr
                fireBaseKeyID = source.fireBaseKeyID
                etateValue = source.etateValue

                // Deep copy produits
                child_14Produits = realmListOf()
                source.child_14Produits.forEach { sourceProduit ->
                    child_14Produits.add(_014_Produits.deepCopy(sourceProduit))
                }
            }
        }

        fun testData(): List<_013_Acheteurs> {
            val data = mutableListOf<_013_Acheteurs>()

            for (k in 1..5) {
                val acheteur = _013_Acheteurs().apply {
                    startDesignation = "_013_Acheteurs $k"
                    tempDateCreationStr = "2025_04_20(12:00)"
                    fireBaseKeyID = "${this.bsonObjectId}->$startDesignation"
                    child_14Produits = realmListOf()
                    // Set random etate for test data
                    etate = Etate.entries.toTypedArray()[(0..5).random()]
                }

                // Create and add products
                val produits = _014_Produits.testData()
                produits.forEach { produit ->
                    acheteur.child_14Produits.add(produit)
                }

                data.add(acheteur)
            }

            return data
        }
    }
}
