package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository

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
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

@Stable
class DAchatOperationComposeRepositoryProtoJuin17(
    private val ancienRepo: D_AchatOperationDataBaseProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val itsTestModel = true
    private val _datas = mutableStateOf<List<D_AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val ouvertD_AchatOperationBsonId = "bon_001"

    // Fixed: Use datasValue instead of _datas.value to ensure proper reactivity
    val filteredDatasValue by derivedStateOf {
        datasValue.filtered()
    }

    fun List<D_AchatOperation>.filtered(): List<D_AchatOperation> {
        val filtered = this.filter {
            findIfItsActiveDataPourActuelleComptApp(it)
        }

        // Add logging to debug the filtering
        Log.d("FilterDebug", "Total items: ${this.size}, Filtered items: ${filtered.size}")
        filtered.forEach {
            Log.d("FilterDebug", "Filtered item: ${it.bsonObjectId}, parentBonVentObjectId: ${it.parentBonVentObjectId}")
        }

        return filtered
    }

    private fun findIfItsActiveDataPourActuelleComptApp(it: D_AchatOperation): Boolean {
        val shouldInclude = it.parentBonVentObjectId == ouvertD_AchatOperationBsonId

        // Log each item being checked
        Log.d("FilterDebug", "Checking item: ${it.bsonObjectId}, parentBonVentObjectId: ${it.parentBonVentObjectId}, target: $ouvertD_AchatOperationBsonId, include: $shouldInclude")

        return shouldInclude
    }

    init {
        composScope.launch {
            if (itsTestModel) {
                val testData = getTestDate()
                _datas.value = testData

                // Log the initial data for debugging
                Log.d("InitDebug", "Initial data size: ${testData.size}")
                testData.forEach {
                    Log.d("InitDebug", "Item: ${it.bsonObjectId}, parentBonVentObjectId: ${it.parentBonVentObjectId}")
                }

                // FIXED: Access derived state from Main dispatcher
                withContext(Dispatchers.Main.immediate) {
                    Log.d("InitDebug", "Filtered data size: ${filteredDatasValue.size}")
                    filteredDatasValue.forEach {
                        Log.d("InitDebug", "Filtered Item: ${it.bsonObjectId}, parentBonVentObjectId: ${it.parentBonVentObjectId}")
                    }
                }
            } else {
                dao.getAllFlow().collect { data ->
                    _datas.value = data
                    Log.d("DatabaseDebug", "Loaded from database: ${data.size} items")

                    // FIXED: Access derived state from Main dispatcher
                    withContext(Dispatchers.Main.immediate) {
                        Log.d("DatabaseDebug", "Filtered data size: ${filteredDatasValue.size}")
                    }
                }
            }
        }
    }

    fun getTestDate(): List<D_AchatOperation> {
        val parentProduitBsonObjectIdPrd1 = "produit_001"
        return listOf(
            D_AchatOperation(
                bsonObjectId = "test_achat_001",
                nomImageFichieOuApellationDuCouleur = "Couleur1",
                parentBonVentObjectId = "bon_001", // This should match ouvertD_AchatOperationBsonId
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd1,
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 1L,
                quantityAchete = 5,
                provisoireMonPrix = 150.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_100",
                nomImageFichieOuApellationDuCouleur = "Couleur2",
                parentBonVentObjectId = "bon_001", // This should match ouvertD_AchatOperationBsonId
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd1,
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 1L,
                quantityAchete = 5,
                provisoireMonPrix = 150.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_002",
                nomImageFichieOuApellationDuCouleur = "Produit Test 2",
                parentBonVentObjectId = "bon_001", // This should match ouvertD_AchatOperationBsonId
                parentProduitBsonObjectId = "produit_002",
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 2L,
                quantityAchete = 3,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            // These ones with "bon_002" should be filtered out (not displayed)
            D_AchatOperation(
                bsonObjectId = "test_achat_003",
                nomImageFichieOuApellationDuCouleur = "Produit Test 3",
                parentBonVentObjectId = "bon_002", // Different bon - should be filtered out
                parentProduitBsonObjectId = "produit_003",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                clientParentObjectId = "client_002",
                produitAcheterAncienID = 3L,
                quantityAchete = 2,
                provisoireMonPrix = 75.5,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.Affiche
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_004",
                nomImageFichieOuApellationDuCouleur = "Produit Test 4",
                parentBonVentObjectId = "bon_002", // Different bon - should be filtered out
                parentProduitBsonObjectId = "produit_004",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                clientParentObjectId = "client_002",
                produitAcheterAncienID = 4L,
                quantityAchete = 1,
                provisoireMonPrix = 500.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            )
        )
    }

    fun addOrUpdateData(data: D_AchatOperation) {
        val dataAvecTigerUpdate = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = datasValue.indexOfFirst { ancien ->
            D_AchatOperation.delimiterExistence(ancien, dataAvecTigerUpdate)
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
