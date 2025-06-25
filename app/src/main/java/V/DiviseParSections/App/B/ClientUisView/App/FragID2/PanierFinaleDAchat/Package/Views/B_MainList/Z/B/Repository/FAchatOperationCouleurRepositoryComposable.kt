package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AppCompt
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9.Companion.createCouleurOnVentKey
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.Modules.D.Glide.Affiche
import Z_CodePartageEntreApps.Modules.D.Glide.FileCouleurInfos
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import java.io.File

@Stable
class FAchatOperationCouleurRepositoryComposable(
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<FCouleurVentOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val datasFiltered by derivedStateOf { datasValue }

    val filteredDatasValue by derivedStateOf {
        datasValue.filter {
            it.etateActuellementEst == FCouleurVentOperation.EtateActuellementEst.ParentBonVentConfirme
        }
    }
    companion object {
        private const val TAG = "ColorOperation"
    }

    init {
        composScope.launch {
            try {
                if (depuitTestData) {
                    withContext(Dispatchers.Main) {
                        _datas.value = getTestDate()
                    }
                } else {
                    dao.getAllFlow().collect { data ->
                        try {
                            withContext(Dispatchers.Main) {
                                _datas.value = data
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun addOrUpdateData(data: FCouleurVentOperation) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            FCouleurVentOperation.compareEntre(ancien = ancien, newData = data)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + data
        }

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
    }


    fun getTestDate(): List<FCouleurVentOperation> {
        return emptyList()
    }

    fun acheterUneCouleur(
        ouvertData: Z_AppCompt,
        produit: ArticlesBasesStatsTable,
        quantity: Int = 1,
        colorIndex: Int
    ) {
        if (quantity <= 0 || colorIndex < 0) {
            return
        }

        composScope.launch {
            try {
                val colorName =
                    getFileCouleurInfosFromProduct(produit, colorIndex).nomCouleurStrSiSonImageDispo

                val couleurVentOperation = createSafeCouleurVentOperation(
                    id = createCouleurOnVentKey(
                        ouvertData,
                        produit,
                        colorIndex
                    ),
                    ouvertData = ouvertData,
                    produit = produit,
                    colorIndex = colorIndex,
                    colorName = colorName,
                    quantity = quantity
                )

                addOrUpdateData(couleurVentOperation)

            } catch (e: OutOfMemoryError) {
                System.gc()
            } catch (e: Exception) {
            }
        }
    }

    private fun createSafeCouleurVentOperation(
        id: String,
        ouvertData: Z_AppCompt,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        colorName: String,
        quantity: Int
    ): FCouleurVentOperation {
        return FCouleurVentOperation(
            keyID = id,
            nomCouleurStrSiSonImageDispo = colorName,
            nomImageFichieOuApellationDuCouleur = "${produit.id}_${colorIndex + 1}",
            aAffiche = Affiche.Nom,
            baseFileName = "${produit.id}_${colorIndex + 1}.webp",
            parentZAppComptID = ouvertData.bsonObjectId,
            parentEPeriodVentId = ouvertData.ouvertF1PeriodVentId,
            parentEPeriodVentStartDate = ouvertData.ouvertF1PeriodVentStartTimesTamp,
            parentBonVentId = ouvertData.ouvertF2BonVentId,
            parentClientId = ouvertData.ouvertClientOnVentKeyId,
            parentClientName = ouvertData.ouvertClientOnVentNom,
            parentProduitId = ouvertData.ouvertF3ProduitOnVentID,
            parentProduitAncienId = ouvertData.ouvertProduitOnVentAncienId,
            parentProduitKeyNom = ouvertData.ouvertProduitOnVentNom,
            quantityAchete = quantity,
            etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.ChoisiQuantityConfirme,
            type = FCouleurVentOperation.Type.CommandeDeLui,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }



    private fun getCouleurNameByIndex(produit: ArticlesBasesStatsTable, colorIndex: Int): String {
        return when (colorIndex) {
            0 -> produit.couleur1 ?: "couleur1"
            1 -> produit.couleur2 ?: "couleur2"
            2 -> produit.couleur3 ?: "couleur3"
            3 -> produit.couleur4 ?: "couleur4"
            else -> "couleur${colorIndex + 1}"
        }
    }

    private fun getFileCouleurInfosFromProduct(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int = 0
    ): FileCouleurInfos {
        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
        val fileName = "${produit.id}_${colorIndex + 1}"

        val imageFile = try {
            listOf("webp", "jpg", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { file ->
                    try {
                        file.exists() && file.canRead() && file.length() > 0
                    } catch (e: Exception) {
                        false
                    }
                }
                ?: File("$basePath/NonTrouve.webp")
        } catch (e: Exception) {
            File("$basePath/NonTrouve.webp")
        }

        val imageExists = try {
            imageFile.name != "NonTrouve.webp" &&
                    imageFile.exists() && imageFile.canRead() && imageFile.length() > 0
        } catch (e: Exception) {
            false
        }

        return FileCouleurInfos(
            keyID = fileName,
            bsonObjectId = BsonObjectId(),
            aAffiche = if (imageExists) Affiche.Image else Affiche.Nom,
            imageCouleurFichie = imageFile,
            nomCouleurStrSiSonImageDispo = getCouleurNameByIndex(produit, colorIndex),
            quantityDeDisponibility = 0,
            colorIndex = colorIndex,
        )
    }
}

@Entity
data class FCouleurVentOperation(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var nomImageFichieOuApellationDuCouleur: String = "",
    var parentBonVentId: String = "",
    var parentProduitId: String = "",
    var nomCouleurStrSiSonImageDispo: String = "",
    var aAffiche: Affiche = Affiche.Image,
    var baseFileName: String = "",
    var parentZAppComptID: String = "",
    var parentEPeriodVentId: String = "",
    var parentEPeriodVentStartDate: Long = 0,
    var parentClientId: String = "",
    var parentClientName: String = "",
    var parentProduitAncienId: Long = 0L,
    var parentProduitKeyNom: String = "",
    var type: Type = Type.CommandeDeLui,
    var quantityAchete: Int = 1,
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,
    var provisoireMonPrix: Double = 0.0,
    var achatParentBsonIDOld: String = "",
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

/*       /*Metode Hash Proto**/
    fun isSameEntity(other: FCouleurVentOperation) =
        keyID == other.keyID ||
                nomImageFichieOuApellationDuCouleur == other.nomImageFichieOuApellationDuCouleur &&
                parentProduitId == other.parentProduitId &&
                parentBonVentId == other.parentBonVentId &&
                parentZAppComptID == other.parentZAppComptID

    override fun hashCode() = Objects.hash(
        keyID,
        nomImageFichieOuApellationDuCouleur,
        parentProduitId,
        parentBonVentId,
        parentZAppComptID
    )

    override fun equals(other: Any?) =
        this === other || (other is FCouleurVentOperation && isSameEntity(other))
                  */

    companion object {
        val ref =
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/FCouleurVentOperation")


        fun compareEntre(
            ancien: FCouleurVentOperation,
            newData: FCouleurVentOperation
        ): Boolean {
            val delimiterExistence =
                ancien.keyID == newData.keyID
            return delimiterExistence
        }
    }
}

