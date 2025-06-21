package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import org.mongodb.kbson.BsonObjectId

@Entity
data class D_AchatOperation(
    @PrimaryKey
    var bsonObjectId: String = BsonObjectId().toHexString(),
    var creationTimesTamp: Long = System.currentTimeMillis(),
    var nomImageFichieOuApellationDuCouleur: String = "",

    // Section Related ParentBsonObjectId
    var parentBonVentObjectId: String = "",
    var parentProduitBsonObjectId: String = "",
    var parentComptVendeurCreateurObjectId: String = "",

    // Section Related Parents Infos
    var clientParentObjectId: String = "",
    var produitAcheterAncienID: Long = 0L,

    // Section StatuesMutable
    var quantityAchete: Int = 0,
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.Affiche,
    var provisoireMonPrix: Double = 0.0,

    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {

    enum class EtateActuellementEst {
        Affiche,
        CONFIRME,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }

    fun withDernierTimeTampsSynchronisationAvecFireBase(): D_AchatOperation {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef = Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/D_AchatOperation")
        fun logCategory(data: D_AchatOperation, TAG: String) { Log.d(TAG, "D_AchatOperation: ${data.bsonObjectId} - ") }
        fun safeRemoveRef(): Unit { caRef.removeValue() }
        fun compareEntre(
            ancien: D_AchatOperation,
            newData: D_AchatOperation
        ): Boolean {
            val delimiterExistence =
                ancien.nomImageFichieOuApellationDuCouleur == newData.nomImageFichieOuApellationDuCouleur &&
                        ancien.parentProduitBsonObjectId == newData.parentProduitBsonObjectId &&
                        ancien.parentBonVentObjectId == newData.parentBonVentObjectId &&
                        ancien.parentComptVendeurCreateurObjectId == newData.parentComptVendeurCreateurObjectId
            return delimiterExistence
        }
    }
}
