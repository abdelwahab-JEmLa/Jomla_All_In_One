package EntreApps.Shared.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.CollectionReference
import org.mongodb.kbson.BsonObjectId

@Entity
data class M16CategorieProduit(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var bsonObjectId: String = RepositorysMainGetter.Companion.getPushFireBase(M09AppCompt.Companion.ref),
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val catalogueParentId: Long = 0,
    val parentCatalogueIdObject: String = "",

    var nom: String = "",

    var position: Int = 0,

    var positionDouble: Double = 0.0,

    var displayedHeader: Boolean = false,

    val itsHeldPourDeplacement: Boolean = false,

    var cSelectionePourDeplace: Boolean = false,
) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): M16CategorieProduit {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "id"                                            to id,
        "bsonObjectId"                                  to bsonObjectId,
        "keyID"                                         to keyID,
        "creationTimestamp"                             to creationTimestamp,
        "dernierTimeTampsSynchronisationAvecFireBase"   to dernierTimeTampsSynchronisationAvecFireBase,
        "catalogueParentId"                             to catalogueParentId,
        "parentCatalogueIdObject"                       to parentCatalogueIdObject,
        "nom"                                           to nom,
        "position"                                      to position,
        "positionDouble"                                to positionDouble,
        "displayedHeader"                               to displayedHeader,
        "itsHeldPourDeplacement"                        to itsHeldPourDeplacement,
        "cSelectionePourDeplace"                        to cSelectionePourDeplace,
    )

    companion object {
        val ref = M00CentralParametresOfAllApps.centralRef
            .child("C_CategorieProduitInfos")

        val refFirestore: CollectionReference = RepositorysMainGetter.firestoreCentralRefData
            .document("M16CategorieProduit")
            .collection("Datas")

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        fun generePushKey() =
            ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(
        ): M16CategorieProduit {
            val data = M16CategorieProduit()
            return data
        }

        fun logCategory(category: M16CategorieProduit, TAG: String) {
            Log.d(
                TAG, "Category selected for displacement processed: " +
                        "ID=${category.id}, Name='${category.nom}', " +
                        "CatalogueParentId=${category.catalogueParentId}, " +
                        "Position=${category.position}, " +
                        "${category.cSelectionePourDeplace}, " +
                        "Timestamp=${category.dernierTimeTampsSynchronisationAvecFireBase}"
            )
        }
    }
}

data class M21CataloguesCategorie(
    var keyID: String = BsonObjectId.Companion().toHexString(),
    val id: Long = 0,
    val nom: String = "",
    val premierCategorieId: Long = 0,
    val position: Int = 0,
    val couleur: Color = Color(0xFF9C27B0)
)

// Static repository function that provides catalogues list
fun get_ListM21CataloguesCategorie(): List<M21CataloguesCategorie> {
    return listOf(
        M21CataloguesCategorie(
            keyID = "t4",
            id = 4,
            nom = "Sans Catalogue",
            premierCategorieId = 0,
            position = 0,
            couleur = Color(0xFF9C27B0) // Purple
        ),
        M21CataloguesCategorie(
            keyID = "t2",
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 1755942163531,
            position = 1,
            couleur = Color(0xFFE91E63) // Pink for cosmetics
        ),
        M21CataloguesCategorie(
            keyID = "t1",
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 1755942577975,
            position = 2,
            couleur = Color(0xFFFF9800) // Orange for confectionery
        ),
        M21CataloguesCategorie(
            keyID = "t3",
            id = 3,
            nom = "TeBnage",
            premierCategorieId = 1755942590731,
            position = 3,
            couleur = Color(0xFF4CAF50) // Green for teenage category
        ),
    )
}
