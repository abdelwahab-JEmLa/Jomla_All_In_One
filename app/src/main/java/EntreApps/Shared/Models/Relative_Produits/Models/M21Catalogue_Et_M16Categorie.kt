package EntreApps.Shared.Models.Relative_Produits.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.CollectionReference

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
        val ref = M00CentralParametresOfAllApps.Companion.centralRef
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

