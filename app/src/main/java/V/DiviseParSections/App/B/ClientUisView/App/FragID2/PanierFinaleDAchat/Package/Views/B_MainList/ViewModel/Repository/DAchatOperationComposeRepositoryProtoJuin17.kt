package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.D_AchatOperationDataBaseProtoJuin17
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
import java.util.Objects

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

    val filteredDatasValue by derivedStateOf {
        datasValue.filter { it.parentBonVentObjectId == ouvertD_AchatOperationBsonId }
    }

    init {
        composScope.launch {
            if (itsTestModel) {
                withContext(Dispatchers.Main.immediate) {
                    _datas.value = getTestDate()
                }
            } else {
                dao.getAllFlow().collect { data ->
                    withContext(Dispatchers.Main.immediate) { _datas.value = data }
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
                parentBonVentObjectId = "bon_001",
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
                parentBonVentObjectId = "bon_001",
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
                parentBonVentObjectId = "bon_001",
                parentProduitBsonObjectId = "produit_002",
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 2L,
                quantityAchete = 3,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_003",
                nomImageFichieOuApellationDuCouleur = "Produit Test 3",
                parentBonVentObjectId = "bon_002", // Different bon to test filtering
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
                parentBonVentObjectId = "bon_002", // Different bon to test filtering
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
        val dataUpdate = data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.isSameEntity(dataUpdate) }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = if (existingIndex >= 0) {
                    datasValue.toMutableList().apply {
                        this[existingIndex] = this[existingIndex].copy(
                            quantityAchete = this[existingIndex].quantityAchete + dataUpdate.quantityAchete,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                    }
                } else datasValue + dataUpdate
            }
        }
        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataUpdate)
    }
}

@Entity
data class D_AchatOperation(
    @PrimaryKey var bsonObjectId: String = BsonObjectId().toHexString(),
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
    enum class EtateActuellementEst { Affiche, CONFIRME, SUPPRIME_AU_PREMIER_PICK, SUPP_AU_PANIER_FINALE }

    fun isSameEntity(other: D_AchatOperation) = nomImageFichieOuApellationDuCouleur == other.nomImageFichieOuApellationDuCouleur &&
            parentProduitBsonObjectId == other.parentProduitBsonObjectId &&
            parentBonVentObjectId == other.parentBonVentObjectId &&
            parentComptVendeurCreateurObjectId == other.parentComptVendeurCreateurObjectId

    override fun equals(other: Any?) = this === other || (other is D_AchatOperation && isSameEntity(other) &&
            quantityAchete == other.quantityAchete && provisoireMonPrix == other.provisoireMonPrix)

    override fun hashCode() = Objects.hash(
        nomImageFichieOuApellationDuCouleur,
        parentProduitBsonObjectId,
        parentBonVentObjectId,
        parentComptVendeurCreateurObjectId,
        quantityAchete,
        provisoireMonPrix
    )

    companion object {
        val caRef = Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/D_AchatOperation")
    }
}
