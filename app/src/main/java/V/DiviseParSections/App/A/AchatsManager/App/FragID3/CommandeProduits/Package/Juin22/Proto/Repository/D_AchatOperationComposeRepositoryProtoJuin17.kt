package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.D_AchatOperation.Companion.delimiterExistence
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.D_AchatOperationDataBaseProtoJuin17
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId

@Stable
class D_AchatOperationComposeRepositoryProtoJuin17(
    private val ancienRepo: D_AchatOperationDataBaseProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<D_AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: D_AchatOperation) {
        val dataAvecTigerUpdate = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = datasValue.indexOfFirst { ancien ->
            delimiterExistence(ancien, dataAvecTigerUpdate)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    quantityAchete = this[existingIndex].quantityAchete + dataAvecTigerUpdate.quantityAchete,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + dataAvecTigerUpdate
        }

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataAvecTigerUpdate)
    }
}

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
        val caRef =
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/D_AchatOperation")

        fun logCategory(data: D_AchatOperation, TAG: String) {
            Log.d(TAG, "D_AchatOperation: ${data.bsonObjectId} - ")
        }

        fun delimiterExistence(
            ancien: D_AchatOperation,
            dataAvecTigerUpdate: D_AchatOperation
        ) =
            ancien.nomImageFichieOuApellationDuCouleur == dataAvecTigerUpdate.nomImageFichieOuApellationDuCouleur &&
                    ancien.parentProduitBsonObjectId == dataAvecTigerUpdate.parentProduitBsonObjectId &&
                    ancien.parentBonVentObjectId == dataAvecTigerUpdate.parentBonVentObjectId &&
                    ancien.parentComptVendeurCreateurObjectId == dataAvecTigerUpdate.parentComptVendeurCreateurObjectId
    }
}
