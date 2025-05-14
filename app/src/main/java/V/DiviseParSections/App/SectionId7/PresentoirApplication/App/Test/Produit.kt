package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

data class Produit(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val infos: ProduitInfos = ProduitInfos(),
    val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
    val clients: List<Client> = emptyList(),
) {
    data class ProduitInfos(
        val nom: String = ""
    )

    data class CesStatuesMutable(
        val cActiveDonsSonListParent: Boolean = false,
        val dateDeModification: Long = System.currentTimeMillis(),
        val estVisible: Boolean = true
    )

    data class Client(
        val id: Long = 0,
        val timestamp: Long = System.currentTimeMillis(),
        val infos: ClientInfos = ClientInfos(),
        val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
        val typesTarification: List<TypeTarification> = emptyList(),
    ) {

        data class ClientInfos(
            val nom: String = ""
        )

        data class CesStatuesMutable(
            val cActiveDonsSonListParent: Boolean = false,
            val produitId: Long = 0, // Reference to parent product ID
            val dateDeModification: Long = System.currentTimeMillis(),
            val estSelectionne: Boolean = false
        )

        data class TypeTarification(
            val id: Long = 0,
            val timestamp: Long = System.currentTimeMillis(),
            val infos: Infos = Infos(),
            val PrixsCurrency: List<Prix> = emptyList(),
        ) {
            data class Infos(
                var type: TypeTarificationEnum = TypeTarificationEnum.NonDefini,
            )

            enum class TypeTarificationEnum {
                NonDefini,
                ParBenifice,
                Historique,
                LeMaxPrixArrive
            }

            data class Prix(
                val id: Long = 0,
                val timestamp: Long = System.currentTimeMillis(),
                val valeur: Double = 0.0,
            )
        }
    }
}

class RealmDataBase {
    var childsLists: RealmList<Produit> = realmListOf()

    class Produit : RealmObject {
        @PrimaryKey
        var bsonObjectId: ObjectId = ObjectId()
        var infosKey: String = "nom"
        var childsList: RealmList<Client> = realmListOf()

        class Client : RealmObject {
            @PrimaryKey
            var bsonObjectId: ObjectId = ObjectId()
            var childsList: RealmList<TypeTarification> = realmListOf()

            class TypeTarification : RealmObject {
                @PrimaryKey
                var bsonObjectId: ObjectId = ObjectId()
                var childsList: RealmList<Tarification> = realmListOf()

                class Tarification : RealmObject {
                    @PrimaryKey
                    var bsonObjectId: ObjectId = ObjectId()
                }
            }
        }
    }
}

