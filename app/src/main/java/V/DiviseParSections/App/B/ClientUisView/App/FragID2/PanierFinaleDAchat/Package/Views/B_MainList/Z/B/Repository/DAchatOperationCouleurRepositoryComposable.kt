package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.C3_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ZAppCompt_RepositoryComposable
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
import java.io.File
import java.util.Objects


object DSubClassFunctionality_CouleurAchatOperation {
    fun getDataDepuitIndex(
        ouvertTransactionCommercial: C3_TransactionCommercial,
        ouvertData_bProduitDataBase_SubClassFunctionality: ArticlesBasesStatsTable,
        nomImageFichieOuApellationDuCouleur: String
    ): D_AchatOperation {
        return D_AchatOperation(
            parentBonVentObjectId = ouvertTransactionCommercial.bsonObjectId,
            parentProduitBsonObjectId = ouvertData_bProduitDataBase_SubClassFunctionality.bsonObjectId,
            parentProduitKeyNom = ouvertData_bProduitDataBase_SubClassFunctionality.nom,

            nomImageFichieOuApellationDuCouleur = nomImageFichieOuApellationDuCouleur,
        )
    }


    fun trouve_nomImageFichieOuApellationDuCouleurPar(
        indexCouleur: Int,
        ouvertData_bProduitDataBase_SubClassFunctionality: ArticlesBasesStatsTable?
    ): String {
        // Get the color name based on the index
        val couleurName = when (indexCouleur) {
            0 -> ouvertData_bProduitDataBase_SubClassFunctionality?.couleur1
            1 -> ouvertData_bProduitDataBase_SubClassFunctionality?.couleur2
            2 -> ouvertData_bProduitDataBase_SubClassFunctionality?.couleur3
            3 -> ouvertData_bProduitDataBase_SubClassFunctionality?.couleur4
            else -> null
        }

        // Return empty string if color is null or blank
        if (couleurName.isNullOrBlank()) {
            return ""
        }

        // Base path for images (same as in CreateCouleurInfosFromProduct.kt)
        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

        // Create the image file name pattern: {articleId}_{imageIndex}
        val imageIndex = indexCouleur + 1
        val baseFileName = "${ouvertData_bProduitDataBase_SubClassFunctionality?.id}_$imageIndex"

        // Check for image file existence with different extensions
        val supportedExtensions = listOf("jpg", "webp", "jpeg", "png")
        val imageFile = supportedExtensions
            .map { extension -> File("$basePath/$baseFileName.$extension") }
            .firstOrNull { file ->
                file.exists() && file.canRead() && file.length() > 0
            }

        // Return image file name (without extension) if image exists, otherwise return color name
        return if (imageFile != null && imageFile.name != "NonTrouve.webp") {
            baseFileName // Return the base file name without extension
        } else {
            couleurName // Return the color name if no image is available
        }
    }

    fun confirmeOldOuvertData(ouvertData_dCouleurAchatOperation_SubClassFunctionality: D_AchatOperation?): D_AchatOperation? {
        return ouvertData_dCouleurAchatOperation_SubClassFunctionality?.copy(
            etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
        )
    }
}

@Stable
class DAchatOperationCouleurRepositoryComposable(
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<D_AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val ouvertDataID = zAppComptRepositoryComposable
        .ouvertData?.bsonObjectId ?: "b1"

    val datasFiltered by derivedStateOf {
        datasValue.filter {
            it.parentComptVendeurCreateurObjectId == ouvertDataID
        }
    }

    val ouvertData by derivedStateOf {
        datasFiltered.lastOrNull {
            it.etateActuellementEst == D_AchatOperation.EtateActuellementEst.CreeSlote

        } ?: D_AchatOperation(parentComptVendeurCreateurObjectId = ouvertDataID)
    }

    fun ouvreData(): Unit {

    }

    fun ouvrireBonVent(clientObjectID: String) {
        addOrUpdateData(
            ouvertData.copy(
                parentBonVentObjectId = BsonObjectId().toHexString(),
                parentClientObjectId = clientObjectID,
                parentEPeriodVentObjectId = "p1",
                parentEPeriodVentStartDate = "juin-24",
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentOuvert
            )
        )
    }

    val ouvertD_AchatOperationBsonId = "bon_001"

    val filteredDatasValue by derivedStateOf {
        datasValue.filter {
            it.parentBonVentObjectId == ouvertD_AchatOperationBsonId
                    && it.etateActuellementEst == D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
        }
    }


    init {
        composScope.launch {
            if (depuitTestData) {
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
                parentClientObjectId = "client_001",
                parentProduitAncienId = 1L,
                quantityAchete = 5,
                provisoireMonPrix = 150.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_100",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd1}2",
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd1,
                parentComptVendeurCreateurObjectId = "vendeur_001",
                parentClientObjectId = "client_001",
                parentProduitAncienId = 1L,
                quantityAchete = 5,
                provisoireMonPrix = 150.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_002",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd2}_1",
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd2,
                parentComptVendeurCreateurObjectId = "vendeur_001",
                parentClientObjectId = "client_001",
                parentProduitAncienId = 2L,
                quantityAchete = 3,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            ),
            D_AchatOperation(
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                bsonObjectId = "test_achat_200",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd3}_2",
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd3,
                parentProduitAncienId = parentProduitAncienIDPrd3.toLong(),
                parentComptVendeurCreateurObjectId = "vendeur_001",
                parentClientObjectId = "client_001",
                quantityAchete = 10,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            ),
            D_AchatOperation(
                parentBonVentObjectId = ouvertD_AchatOperationBsonId,
                bsonObjectId = "test_achat_300",
                nomImageFichieOuApellationDuCouleur = "${parentProduitAncienIDPrd3}_1",
                parentProduitBsonObjectId = parentProduitBsonObjectIdPrd3,
                parentProduitAncienId = parentProduitAncienIDPrd3.toLong(),
                parentComptVendeurCreateurObjectId = "vendeur_001",
                parentClientObjectId = "client_001",
                quantityAchete = 5,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_003",
                nomImageFichieOuApellationDuCouleur = "Produit Test 3",
                parentBonVentObjectId = "bon_002",
                parentProduitBsonObjectId = "produit_003",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                parentClientObjectId = "client_002",
                parentProduitAncienId = 3L,
                quantityAchete = 2,
                provisoireMonPrix = 75.5,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CreeSlote
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_004",
                nomImageFichieOuApellationDuCouleur = "Produit Test 4",
                parentBonVentObjectId = "bon_002",
                parentProduitBsonObjectId = "produit_004",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                parentClientObjectId = "client_002",
                parentProduitAncienId = 4L,
                quantityAchete = 1,
                provisoireMonPrix = 500.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.ParentBonVentConfirme
            )
        )
    }

    fun addOrUpdateData(data: D_AchatOperation) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
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

    // Class FastNestedIn Infos
    //Section Parent Period Vent
    var parentEPeriodVentObjectId: String = "",
    var parentEPeriodVentStartDate: String = "",

    //Section Parent Transaction
    var parentClientObjectId: String = "",
    var parentClientKenName: String = "",
    //Section parentProduitAncien
    var parentProduitAncienId: Long = 0L,
    var parentProduitKeyNom: String = "",

    var type: Type = Type.CommandeDeLui,

    var achatParentBsonIDOld: String = "",

    // Section StatuesMutable
    var quantityAchete: Int = 1,
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.CreeSlote,
    var provisoireMonPrix: Double = 0.0,

    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    enum class EtateActuellementEst {
        CreeSlote,
        ParentBonVentOuvert,
        ParentProduitOuvert,
        ChoisiQuantityDialogOuvert,
        ChoisiQuantityConfirme,
        ParentProduitConfirme,
        ParentBonVentConfirme,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE,
    }

    enum class Type { SiNonDispo, CommandeDeLui }

    fun isSameEntity(other: D_AchatOperation) =
        nomImageFichieOuApellationDuCouleur == other.nomImageFichieOuApellationDuCouleur &&
                parentProduitBsonObjectId == other.parentProduitBsonObjectId &&
                parentBonVentObjectId == other.parentBonVentObjectId &&
                parentComptVendeurCreateurObjectId == other.parentComptVendeurCreateurObjectId

    override fun equals(other: Any?) =
        this === other || (other is D_AchatOperation && isSameEntity(other) &&
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
        val caRef =
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/D_AchatOperation")
    }
}
