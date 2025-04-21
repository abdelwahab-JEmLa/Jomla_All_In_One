package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.getCurrentDataTimeString
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _014_Produits : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var startDesignation: String = ""
    var tempDateCreationStr: String = getCurrentDataTimeString()

    var idProduit: Long=0

    var fireBaseKeyID: String = "${this.idProduit}=(${startDesignation})"

    var quantity: Int = 0

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val START_DESIGNATION = "startDesignation"
            const val TEMP_DATE_CREATION = "tempDateCreationStr"
            const val ID_PRODUIT = "idProduit"
            const val QUANTITY = "quantity"
        }

        fun testData(): List<_014_Produits> {
            val data = mutableListOf<_014_Produits>()

            for (k in 1..5) {
                data.add(_014_Produits().apply {
                    startDesignation = "_014_Produits $k"
                    this.tempDateCreationStr = "2025_04_20(12:00)"
                    fireBaseKeyID = "${this.bsonObjectId}->${startDesignation}"
                    quantity = k * 2
                })
            }
            return data
        }

        fun mapDatas(datas: List<_014_Produits>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.START_DESIGNATION to data.startDesignation,
                    SchemaFields.TEMP_DATE_CREATION to data.tempDateCreationStr,
                    SchemaFields.ID_PRODUIT to data.idProduit,
                    SchemaFields.QUANTITY to data.quantity,
                )
            }
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _014_Produits? {
            val produitKey = snapshot.key ?: return null

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

                return _014_Produits().apply {
                    fireBaseKeyID = produitKey
                    this.bsonObjectId = objectId
                    startDesignation = snapshot.child(SchemaFields.START_DESIGNATION).getValue(String::class.java) ?: ""
                    tempDateCreationStr = snapshot.child(SchemaFields.TEMP_DATE_CREATION).getValue(String::class.java) ?: "yyyy_MM_dd(HH:mm)"
                    idProduit = snapshot.child(SchemaFields.ID_PRODUIT).getValue(Long::class.java) ?: 0L
                    quantity = snapshot.child(SchemaFields.QUANTITY).getValue(Int::class.java) ?: 0
                }
            } catch (e: Exception) {
                return null
            }
        }
        fun deepCopy(source: _014_Produits): _014_Produits {
            return _014_Produits().apply {
                this.bsonObjectId = source.bsonObjectId
                startDesignation = source.startDesignation
                this.tempDateCreationStr = source.tempDateCreationStr
                fireBaseKeyID = source.fireBaseKeyID
                quantity = source.quantity
            }
        }
    }
}
