package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.getCurrentDataTimeString
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _013_Acheteurs : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var idClient: Long = 0L
    var nomClient: String = ""

    var startDesignation: String = ""
    var tempDateCreationStr: String = getCurrentDataTimeString()

    var fireBaseKeyID: String = "${idClient}=${tempDateCreationStr}"

    var child_14Produits: RealmList<_014_Produits> = realmListOf()

    var child_14A_HistoriquesDeCetteJour: RealmList<_14A_HistoriuesDeCetteJour> = realmListOf()

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val ID_CLIENT = "idClient"
            const val NOM_CLIENT = "nomClient"
            const val START_DESIGNATION = "startDesignation"
            const val TEMP_DATE_CREATION = "tempDateCreationStr"
            const val CHILD_PRODUITS = "child_14Produits"
            const val child_14A_HistoriquesDeCetteJour = "child_14A_HistoriquesDeCetteJour"
        }

        fun mapDatas(datas: List<_013_Acheteurs>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.ID_CLIENT to data.idClient,
                    SchemaFields.NOM_CLIENT to data.nomClient,
                    SchemaFields.START_DESIGNATION to data.startDesignation,
                    SchemaFields.TEMP_DATE_CREATION to data.tempDateCreationStr,
                    SchemaFields.CHILD_PRODUITS to _014_Produits.mapDatas(data.child_14Produits),
                    SchemaFields.child_14A_HistoriquesDeCetteJour to _14A_HistoriuesDeCetteJour.mapDatas(data.child_14A_HistoriquesDeCetteJour),
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
                    nomClient = snapshot.child(SchemaFields.NOM_CLIENT).getValue(String::class.java) ?: ""
                    startDesignation = snapshot.child(SchemaFields.START_DESIGNATION).getValue(String::class.java) ?: ""
                    tempDateCreationStr = snapshot.child(SchemaFields.TEMP_DATE_CREATION).getValue(String::class.java) ?: "yyyy.mm.dd(HH:mm)"
                    child_14Produits = realmListOf()
                    child_14A_HistoriquesDeCetteJour = realmListOf()
                }

                // Parse produits
                val produitsSnapshot = snapshot.child(SchemaFields.CHILD_PRODUITS)
                produitsSnapshot.children.forEach { produitSnapshot ->
                    val produit = _014_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                    acheteur.child_14Produits.add(produit)
                }

                // Parse historiques
                val historiquesSnapshot = snapshot.child(SchemaFields.child_14A_HistoriquesDeCetteJour)
                historiquesSnapshot.children.forEach { historiqueSnapshot ->
                    val historique = _14A_HistoriuesDeCetteJour.parseDataFromSnapshot(historiqueSnapshot)
                        ?: return@forEach
                    acheteur.child_14A_HistoriquesDeCetteJour.add(historique)
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
                nomClient = source.nomClient
                startDesignation = source.startDesignation
                tempDateCreationStr = source.tempDateCreationStr
                fireBaseKeyID = source.fireBaseKeyID

                // Deep copy produits
                child_14Produits = realmListOf()
                source.child_14Produits.forEach { sourceProduit ->
                    child_14Produits.add(_014_Produits.deepCopy(sourceProduit))
                }
                // Deep copy produits
                child_14A_HistoriquesDeCetteJour = realmListOf()
                source.child_14A_HistoriquesDeCetteJour.forEach { sourceProduit ->
                    child_14A_HistoriquesDeCetteJour.add(_14A_HistoriuesDeCetteJour.deepCopy(sourceProduit))
                }


            }
        }

        fun testData(): List<_013_Acheteurs> {
            val data = mutableListOf<_013_Acheteurs>()

            for (k in 1..5) {
                val acheteur = _013_Acheteurs().apply {
                    idClient = k.toLong()
                    nomClient = "Client $k"
                    startDesignation = "_013_Acheteurs $k"
                    tempDateCreationStr = "2025_04_20(12:00)"
                    fireBaseKeyID = "${bsonObjectId}->$startDesignation"
                    child_14Produits = realmListOf()
                    child_14A_HistoriquesDeCetteJour = realmListOf()
                }

                // Create and add products
                val produits = _014_Produits.testData()
                produits.forEach { produit ->
                    acheteur.child_14Produits.add(produit)
                }

                // Create and add historiques
                val historiques = _14A_HistoriuesDeCetteJour.testData()
                historiques.forEach { historique ->
                    acheteur.child_14A_HistoriquesDeCetteJour.add(historique)
                }

                data.add(acheteur)
            }

            return data
        }
    }
}
