package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.Modules.D.Glide.Affiche
import Z_CodePartageEntreApps.Modules.D.Glide.FileCouleurInfos
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
        Log.d(TAG, "=== addOrUpdateData START ===")
        Log.d(TAG, "Input data - keyID: ${data.keyID}")
        Log.d(TAG, "Input data - quantityAchete: ${data.quantityAchete}")
        Log.d(TAG, "Input data - etateActuellementEst: ${data.etateActuellementEst}")
        Log.d(TAG, "Input data - parentProduitId: ${data.parentProduitId}")
        Log.d(TAG, "Input data - parentCouleurDataBaseKey: ${data.parentCouleurDataBaseKey}")

        Log.d(TAG, "Current datasValue size: ${datasValue.size}")
        datasValue.forEachIndexed { index, item ->
            Log.d(TAG, "Existing[$index] - keyID: ${item.keyID}, quantity: ${item.quantityAchete}")
        }

        val existingIndex = datasValue.indexOfFirst { ancien ->
            val isSame = FCouleurVentOperation.isSame(ancien = ancien, newData = data)
            Log.d(TAG, "Comparing with existing item:")
            Log.d(TAG, "  Existing - keyID: ${ancien.keyID}, quantity: ${ancien.quantityAchete}")
            Log.d(TAG, "  New - keyID: ${data.keyID}, quantity: ${data.quantityAchete}")
            Log.d(TAG, "  isSame result: $isSame")
            isSame
        }

        Log.d(TAG, "Found existingIndex: $existingIndex")

        _datas.value = if (existingIndex >= 0) {
            Log.d(TAG, "UPDATING existing item at index $existingIndex")
            val existingItem = datasValue[existingIndex]
            Log.d(TAG, "Before update - existing quantity: ${existingItem.quantityAchete}")
            Log.d(TAG, "Before update - existing keyID: ${existingItem.keyID}")

            datasValue.toMutableList().apply {
                val updatedItem = data.copy(
                    keyID = existingItem.keyID, // Keep the original keyID
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                Log.d(TAG, "Updated item - keyID: ${updatedItem.keyID}")
                Log.d(TAG, "Updated item - quantityAchete: ${updatedItem.quantityAchete}")
                Log.d(TAG, "Updated item - etateActuellementEst: ${updatedItem.etateActuellementEst}")

                this[existingIndex] = updatedItem

                Log.d(TAG, "After assignment - item[$existingIndex].quantityAchete: ${this[existingIndex].quantityAchete}")
            }
        } else {
            Log.d(TAG, "ADDING new item")
            val newItem = data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            Log.d(TAG, "New item - keyID: ${newItem.keyID}")
            Log.d(TAG, "New item - quantityAchete: ${newItem.quantityAchete}")

            datasValue + newItem
        }

        Log.d(TAG, "After update - datasValue size: ${_datas.value.size}")
        _datas.value.forEachIndexed { index, item ->
            Log.d(TAG, "Final[$index] - keyID: ${item.keyID}, quantity: ${item.quantityAchete}")
        }

        // Update the database repository
        val dataForRepo = if (existingIndex >= 0) {
            data.copy(
                keyID = datasValue[existingIndex].keyID,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        } else {
            data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }

        Log.d(TAG, "Calling ancienRepo.addOrUpdatedAncienRepo with:")
        Log.d(TAG, "  existingIndex: $existingIndex")
        Log.d(TAG, "  data.keyID: ${dataForRepo.keyID}")
        Log.d(TAG, "  data.quantityAchete: ${dataForRepo.quantityAchete}")

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataForRepo)

        Log.d(TAG, "=== addOrUpdateData END ===")
    }
    fun getTestDate(): List<FCouleurVentOperation> {
        return emptyList()
    }

    fun acheterUneCouleur(
        ouvertData: Z_AppCompt,
        relatedCouleur: B1CouleurOuGoutProduitDataBase,
        quantity: Int,
    ) {
        composScope.launch {
            try {
                val couleurVentOperation = createSafeCouleurVentOperation(
                    relatedCouleur = relatedCouleur,
                    ouvertData = ouvertData,
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
        relatedCouleur: B1CouleurOuGoutProduitDataBase,
        ouvertData: Z_AppCompt,
        quantity: Int
    ): FCouleurVentOperation {
        return FCouleurVentOperation(
            parentCouleurDataBaseKey = relatedCouleur.key,

            parentProduitId = relatedCouleur.parentBProduitOldID.toString(),
            parentProduitAncienId = relatedCouleur.parentBProduitOldID,
            parentProduitKeyNom = relatedCouleur.parentBProduitNom,

            parentZAppComptID = ouvertData.bsonObjectId,
            parentEPeriodVentId = ouvertData.ouvertF1PeriodVentId,
            parentEPeriodVentStartDate = ouvertData.ouvertF1PeriodVentStartTimesTamp,
            parentBonVentId = ouvertData.ouvertF2BonVentId,
            parentClientId = ouvertData.ouvertClientOnVentKeyId,
            parentClientName = ouvertData.ouvertClientOnVentNom,
            quantityAchete = quantity,
            etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.ChoisiQuantityConfirme,
            type = FCouleurVentOperation.Type.CommandeDeLui,
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
    var parentCouleurDataBaseKey: String = "",
    var parentBonVentId: String = "",
    var parentProduitId: String = "",
    var parentZAppComptID: String = "",
    var parentEPeriodVentId: String = "",
    var parentEPeriodVentStartDate: Long = 0,
    var parentClientId: String = "",
    var parentClientName: String = "",
    var parentProduitAncienId: Long = 0L,
    var parentProduitKeyNom: String = "",
    var type: Type = Type.CommandeDeLui,
    var quantityAchete: Int = 0,
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


        fun isSame(
            ancien: FCouleurVentOperation,
            newData: FCouleurVentOperation
        ): Boolean {
            val delimiterExistence =
                ancien.keyID == newData.keyID

            return delimiterExistence
        }
    }
}

