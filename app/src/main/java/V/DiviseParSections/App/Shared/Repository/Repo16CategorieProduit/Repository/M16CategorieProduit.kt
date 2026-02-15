package V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class M16CategorieProduit(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var bsonObjectId: String = RepositorysMainGetter.Companion.getPushFireBase(Z_AppCompt.Companion.ref),
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

    companion object {
        val ref =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")

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
