package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
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
class DCouleurAchatOperationRepositoryComposable(
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val itsTestModel = true
    private val _datas = mutableStateOf<List<D_AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val ouvertD_AchatOperationBsonId = "bon_001"

    val filteredDatasValue by derivedStateOf {
        datasValue.filter {
            it.parentBonVentObjectId == ouvertD_AchatOperationBsonId
                    && it.etateActuellementEst == D_AchatOperation.EtateActuellementEst.CONFIRME
        }
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
        val parentProduitBsonObjectIdPrd1 = "684fdff4df922c49f82958c0"
        val parentProduitAncienIDPrd1 = "822_"

        val parentProduitBsonObjectIdPrd2 = "684fdff4df922c49f82958f9"
        val parentProduitAncienIDPrd2 = "881"

        val parentProduitBsonObjectIdPrd3 = "684fdff4df922c49f829570d"
        val parentProduitAncienIDPrd3 = "25"
        return listOf(
            D_AchatOperation(
                bsonObjectId = "test_achat_001",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd1}1",
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
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
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd1}2",
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
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
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd2}_1",
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd2,
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 2L,
                quantityAchete = 3,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                bsonObjectId = "test_achat_200",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd3}_2",
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd3,
                produitAcheterAncienID = parentProduitAncienIDPrd3.toLong(),
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                quantityAchete = 10,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                bsonObjectId = "test_achat_300",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd3}_1",
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd3,
                produitAcheterAncienID = parentProduitAncienIDPrd3.toLong(),
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                quantityAchete = 5,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_003",
                nomImageFichieOuApellationDuCouleur = "Produit Test 3",
                parentBonVentObjectId = "bon_002",
                parentProduitBsonObjectId = "produit_003",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                clientParentObjectId = "client_002",
                produitAcheterAncienID = 3L,
                quantityAchete = 2,
                provisoireMonPrix = 75.5,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ClickOuvre
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_004",
                nomImageFichieOuApellationDuCouleur = "Produit Test 4",
                parentBonVentObjectId = "bon_002",
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
    // Section Foreign BsonIDs
    var nomImageFichieOuApellationDuCouleur: String = "",
    var parentBonVentObjectId: String = "",
    var parentProduitBsonObjectId: String = "",

    // Section Related Parents Infos
    var parentComptVendeurCreateurObjectId: String = "",
    var clientParentObjectId: String = "",
    var produitAcheterAncienID: Long = 0L,

    var type: Type = Type.CommandeDeLui,

    var achatParentBsonID: String = "",

    // Section StatuesMutable
    var quantityAchete: Int = 1,
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.ClickOuvre,
    var provisoireMonPrix: Double = 0.0,

    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    enum class EtateActuellementEst { ClickOuvre,
        CONFIRME, SUPPRIME_AU_PREMIER_PICK, SUPP_AU_PANIER_FINALE }

    enum class Type { SiNonDispo, CommandeDeLui }

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
