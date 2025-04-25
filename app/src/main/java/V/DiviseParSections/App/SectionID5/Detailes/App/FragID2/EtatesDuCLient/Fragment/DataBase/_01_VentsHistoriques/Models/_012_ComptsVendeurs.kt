package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _012_ComptsVendeurs : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var idCompt: Long = 0L
    var startDesignation: String = "_012_ComptsVendeurs $idCompt"

    var fireBaseKeyID: String = "${idCompt}-(${startDesignation})"

    var child_013_Acheteurs: RealmList<_013_ClientTransaction> = realmListOf()

    companion object {
        object SchemaFields {
            const val BSON_OBJECT_ID = "bsonObjectId"
            const val FIREBASE_KEY_ID = "fireBaseKeyID"  // Corrected to match actual property name
            const val ID_COMPT = "idCompt"
            const val START_DESIGNATION = "startDesignation"
            const val CHILD_ACHETEURS = "child_013_Acheteurs"
        }

        fun map_012_ComptsVendeurs(vendeurs: List<_012_ComptsVendeurs>): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.fireBaseKeyID

                validVendeurKey to mapOf(
                    SchemaFields.BSON_OBJECT_ID to vendeur.bsonObjectId.toString(),
                    SchemaFields.ID_COMPT to vendeur.idCompt,
                    SchemaFields.START_DESIGNATION to vendeur.startDesignation,
                    SchemaFields.CHILD_ACHETEURS to _013_ClientTransaction.mapDatas(vendeur.child_013_Acheteurs)
                )
            }
        }

        fun parse_012_ComptsVendeursFromSnapshot(snapshot: DataSnapshot): _012_ComptsVendeurs? {
            val vendeurKey = snapshot.key ?: return null

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

                val vendeur = _012_ComptsVendeurs().apply {
                    fireBaseKeyID = vendeurKey
                    bsonObjectId = objectId
                    idCompt = snapshot.child(SchemaFields.ID_COMPT).getValue(Long::class.java) ?: 0L
                    startDesignation = snapshot.child(SchemaFields.START_DESIGNATION).getValue(String::class.java) ?: ""
                    child_013_Acheteurs = realmListOf()
                }

                val acheteursSnapshot = snapshot.child(SchemaFields.CHILD_ACHETEURS)
                acheteursSnapshot.children.forEach { acheteurSnapshot ->
                    val acheteur = _013_ClientTransaction.parse_13_AcheteursFromSnapshot(acheteurSnapshot)
                        ?: return@forEach
                    vendeur.child_013_Acheteurs.add(acheteur)
                }

                return vendeur
            } catch (e: Exception) {
                return null
            }
        }

        fun createVendeur(id: Long, nom: String, vendeurKey: String): _012_ComptsVendeurs {
            return _012_ComptsVendeurs().apply {
                fireBaseKeyID = vendeurKey
                this.idCompt = id
                startDesignation = nom
                child_013_Acheteurs = realmListOf()

                // Create and add acheteurs
                val acheteursList = _013_ClientTransaction.testData()
                acheteursList.forEach { acheteur ->
                    child_013_Acheteurs.add(acheteur)
                }
            }
        }

        fun deepCopy(source: _012_ComptsVendeurs): _012_ComptsVendeurs {
            return _012_ComptsVendeurs().apply {
                bsonObjectId = source.bsonObjectId
                idCompt = source.idCompt
                startDesignation = source.startDesignation
                fireBaseKeyID = source.fireBaseKeyID

                // Deep copy acheteurs
                child_013_Acheteurs = realmListOf()
                source.child_013_Acheteurs.forEach { sourceAcheteur ->
                    child_013_Acheteurs.add(_013_ClientTransaction.deepCopy(sourceAcheteur))
                }
            }
        }
    }
}
