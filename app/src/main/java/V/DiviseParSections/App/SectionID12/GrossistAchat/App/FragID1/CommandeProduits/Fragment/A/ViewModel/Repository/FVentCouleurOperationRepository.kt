package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.GrossistAchat.Fragment.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class FVentCouleurOperationRepository(
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<FCouleurVentOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val onVentFilteredDatas by derivedStateOf {
        datasValue.filter {
            it.parentGBonVentKeyId == zAppComptRepositoryComposable.currentAppCompt?.onVentGBonVentKeyId
        }
    }
    val onVentFilteredDatasGroupedParProduitKey by derivedStateOf {
        onVentFilteredDatas.groupBy { it.parentBProduitKeyId }
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
            FCouleurVentOperation.isSame(ancien = ancien, newData = data)
        }

        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                val updatedItem = data.copy(
                    keyID = datasValue[existingIndex].keyID,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                this[existingIndex] = updatedItem
            }
        } else {
            val newItem = data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            datasValue + newItem
        }

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

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataForRepo)
    }

    fun getTestDate(): List<FCouleurVentOperation> {
        return emptyList()
    }

    fun acheterUneCouleur(
        zCompt: Z_AppCompt,
        relatedVentOperation: B1CouleurOuGoutProduitDataBase,
        quantity: Int,
    ) {
        composScope.launch {
            try {
                val couleurVentOperation = createSafeCouleurVentOperation(
                    relatedCouleur = relatedVentOperation,
                    zCompt = zCompt,
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
        zCompt: Z_AppCompt,
        quantity: Int
    ): FCouleurVentOperation {
        return FCouleurVentOperation(
            parentCouleurDataBaseKey = relatedCouleur.key,

            parentEVentPeriodKeyId = zCompt.onVentHPeriodVentKeyId,
            parentGBonVentKeyId = zCompt.onVentGBonVentKeyId,

            parentBProduitKeyId = relatedCouleur.parentBProduitOldID.toString(),
            parentBProduitNomDebug = relatedCouleur.parentBProduitNom,

            parentZAppComptID = zCompt.bsonObjectId,

            parentClientName = zCompt.onVentFClientDebugNameKey,
            quantityAchete = quantity,
            etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.ChoisiQuantityConfirme,
            type = FCouleurVentOperation.Type.CommandeDeLui,
        )
    }

    companion object {
        private const val TAG = "ColorOperation"
    }
}

@Entity
data class FCouleurVentOperation(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var parentCouleurDataBaseKey: String = "",

    var parentEVentPeriodKeyId: String = "",
    var parentEVentPeriodDebugName: String = "",

    var parentBProduitKeyId: String = "",
    var parentBProduitNomDebug: String = "",


    var parentEPeriodVentStartDate: Long = 0,
    var parentGBonVentKeyId: String = "",

    var parentZAppComptID: String = "",

    var parentClientId: String = "",
    var parentClientName: String = "",
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

