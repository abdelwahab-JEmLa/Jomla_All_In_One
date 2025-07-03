package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Id9AppComptRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
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
    val zAppComptRepositoryComposable: Id9AppComptRepository,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<FCouleurVentOperationInfos>>(emptyList())
    val datasValue by derivedStateOf {
        _datas.value
    }
    val datasFiltered by derivedStateOf {
        _datas.value
    }

    val onVentFilteredDatas by derivedStateOf {
        datasValue.filter {
            it.parentGBonVentKeyId == zAppComptRepositoryComposable.currentAppCompt?.onVentId8BonVentKeyId
        }
    }
    val datasFilteredParCurrentHVentPeriod by derivedStateOf {
        datasValue.filter {
            it.parentHVentPeriodKeyId == zAppComptRepositoryComposable.currentAppCompt?.onVentHVentPeriodKeyId
        }
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


    fun addOrUpdateData(data: FCouleurVentOperationInfos) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            FCouleurVentOperationInfos.isSame(ancien = ancien, newData = data)
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

    fun getTestDate(): List<FCouleurVentOperationInfos> {
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
    ): FCouleurVentOperationInfos {
        return FCouleurVentOperationInfos(
            parentCouleurInfosKeyID = relatedCouleur.key,

            parentHVentPeriodKeyId = zCompt.onVentHVentPeriodKeyId,

            parentGBonVentKeyId = zCompt.onVentId8BonVentKeyId,

            parentBProduitInfosKeyId = relatedCouleur.parentBProduitInfosKeyID,
            parentProduitInfosOldId = relatedCouleur.parentBProduitOldID,
            parentBProduitNomDebug = relatedCouleur.parentId1ProduitInfosDebugName,

            parentZAppComptID = zCompt.bsonObjectId,

            quantityAchete = quantity,
            etateActuellementEst = FCouleurVentOperationInfos.EtateActuellementEst.ChoisiQuantityConfirme,
            type = FCouleurVentOperationInfos.Type.CommandeDeLui,
        )
    }

    fun delete(data: FCouleurVentOperationInfos) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                ancienRepo.delete(data)

            } catch (e: Exception) {
            }
        }
    }

    companion object {
        private const val TAG = "ColorOperation"

        fun String?.findData(repo: FVentCouleurOperationRepository) = repo.datasValue.find { it.keyID == this }
    }
}

@Entity
data class FCouleurVentOperationInfos(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var parentCouleurInfosKeyID: String = "",

    var parentHVentPeriodKeyId: String = "",
    var parentEVentPeriodDebugName: String = "",

    var parentBProduitInfosKeyId: String = "",
    var parentBProduitNomDebug: String = "",
    var parentProduitInfosOldId: Long = 0,


    var parentEPeriodVentStartDate: Long = 0,
    var parentGBonVentKeyId: String = "",

    //Mutable
    var quantityAchete: Int = 0,
    var provisoireMonPrix: Double = 0.0,
    var etateDelivery: EtateDelivery = EtateDelivery.Trouve,

    var parentZAppComptID: String = "",
    var parentClientInfosKeyID: String = "",
    var parentClientName: String = "",
    var type: Type = Type.CommandeDeLui,
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,
    var achatParentBsonIDOld: String = "",
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val parentDebugInfosID9AppCompt:String ="",
    val parentDebugInfosID7VentPeriod:String="",
    val parentDebugInfosID8BonVent:String="",
    val parentDebugInfosID1Produit:String="",
    val parentDebugInfosID3CouleurProduit:String="",
) {
    data class Parents(
        val parentDebugInfosID9AppCompt:String ="",
        val parentDebugInfosID7VentPeriod:String="",
        val parentDebugInfosID1Produit:String="",
        val parentDebugInfosID3CouleurProduit:String="",
    )

    enum class EtateDelivery{
        Trouve,
        NonTrouve
    }

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

    companion object {
        val ref =
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/FCouleurVentOperationInfos")


        fun isSame(
            ancien: FCouleurVentOperationInfos,
            newData: FCouleurVentOperationInfos
        ): Boolean {
            val delimiterExistence =
                ancien.keyID == newData.keyID

            return delimiterExistence
        }
    }
}
