package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique.Companion.getCurrentDataTimeString
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._14_TransactionStatue.Companion.getCurrentDateString
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._14_TransactionStatue.Companion.getCurrentTimeString
import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _013_ClientTransaction : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var idClient: Long = 0L
    var nomClient: String = ""

    var startDesignation: String = ""
    var tempDateCreationStr: String = getCurrentDataTimeString()

    var dateCreationStr: String = getCurrentDateString()
    var tempCreationStr: String = getCurrentTimeString()

    var fireBaseKeyID: String = "${idClient}=${tempDateCreationStr}"

    var child_14Produits: RealmList<_015_Produits> = realmListOf()

    var child_14A_HistoriquesDeCetteJour: RealmList<_14_TransactionStatue> = realmListOf()

    companion object {
        // Schema constants for consistency
        object SchemaFields {
            const val BSON_OBJECT_ID = "key"
            const val ID_CLIENT = "idClient"
            const val NOM_CLIENT = "nomClient"
            const val START_DESIGNATION = "startDesignation"
            const val TEMP_DATE_CREATION = "tempDateCreationStr"
            const val CHILD_PRODUITS = "child_14Produits"
            const val child_14A_HistoriquesDeCetteJour = "child_14A_HistoriquesDeCetteJour"
        }


        fun mapDatas(datas: List<_013_ClientTransaction>): Map<String, Any> {
            return datas.associate { data ->
                data.fireBaseKeyID to mapOf(
                    SchemaFields.BSON_OBJECT_ID to data.bsonObjectId.toString(),
                    SchemaFields.ID_CLIENT to data.idClient,
                    SchemaFields.NOM_CLIENT to data.nomClient,
                    SchemaFields.START_DESIGNATION to data.startDesignation,
                    SchemaFields.TEMP_DATE_CREATION to data.tempDateCreationStr,
                    SchemaFields.CHILD_PRODUITS to _015_Produits.mapDatas(data.child_14Produits),
                    SchemaFields.child_14A_HistoriquesDeCetteJour to _14_TransactionStatue.mapDatas(
                        data.child_14A_HistoriquesDeCetteJour
                    ),
                )
            }
        }

        fun parse_13_AcheteursFromSnapshot(snapshot: DataSnapshot): _013_ClientTransaction? {
            val acheteurKey = snapshot.key ?: return null

            try {
                // Extract ObjectId if available, or create addNew new one
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

                val acheteur = _013_ClientTransaction().apply {
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
                    val produit = _015_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                    acheteur.child_14Produits.add(produit)
                }

                // Parse historiques
                val historiquesSnapshot = snapshot.child(SchemaFields.child_14A_HistoriquesDeCetteJour)
                historiquesSnapshot.children.forEach { historiqueSnapshot ->
                    val historique = _14_TransactionStatue.parseDataFromSnapshot(
                        historiqueSnapshot
                    )
                        ?: return@forEach
                    acheteur.child_14A_HistoriquesDeCetteJour.add(historique)
                }

                return acheteur
            } catch (e: Exception) {
                return null
            }
        }

        fun deepCopy(source: _013_ClientTransaction): _013_ClientTransaction {
            return _013_ClientTransaction().apply {
                this.bsonObjectId = source.bsonObjectId
                idClient = source.idClient
                nomClient = source.nomClient
                startDesignation = source.startDesignation
                tempDateCreationStr = source.tempDateCreationStr
                fireBaseKeyID = source.fireBaseKeyID

                // Deep copy produits
                child_14Produits = realmListOf()
                source.child_14Produits.forEach { sourceProduit ->
                    child_14Produits.add(_015_Produits.deepCopy(sourceProduit))
                }
                // Deep copy produits
                child_14A_HistoriquesDeCetteJour = realmListOf()
                source.child_14A_HistoriquesDeCetteJour.forEach { sourceProduit ->
                    child_14A_HistoriquesDeCetteJour.add(
                        _14_TransactionStatue.deepCopy(
                            sourceProduit
                        )
                    )
                }
            }
        }

        fun testData(): List<_013_ClientTransaction> {
            val data = mutableListOf<_013_ClientTransaction>()

            for (k in 1..5) {
                val acheteur = _013_ClientTransaction().apply {
                    idClient = k.toLong()
                    nomClient = "ClientAchteur $k"
                    startDesignation = "_013_ClientTransaction $k"
                    tempDateCreationStr = "2025_04_20(12:00)"
                    fireBaseKeyID = "${bsonObjectId}->$startDesignation"
                    child_14Produits = realmListOf()
                    child_14A_HistoriquesDeCetteJour = realmListOf()
                }

                // Create and upsert products
                val produits = _015_Produits.testData()
                produits.forEach { produit ->
                    acheteur.child_14Produits.add(produit)
                }



                data.add(acheteur)
            }

            return data
        }
    }
}
