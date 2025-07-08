package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
class Repo10OperationVentCouleur(
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<M10OperationVentCouleur>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val datasFiltered by derivedStateOf { _datas.value }

    val onVentFilteredDatas by derivedStateOf {
        val targetKey = zAppComptRepositoryComposable.currentAppCompt?.onVentM8BonVentKey
        datasValue.filter { it.parentM8BonVentKeyId == targetKey }
    }

    val datasFilteredParCurrentHVentPeriod by derivedStateOf {
        val targetKey = zAppComptRepositoryComposable.currentAppCompt?.current_OnVent_M14VentPeriode_KeyID
        datasValue.filter { it.parentHVentPeriodKeyId == targetKey }
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
                            // Error handling
                        }
                    }
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun addOrUpdateData(data: M10OperationVentCouleur) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            M10OperationVentCouleur.isSame(ancien = ancien, newData = data)
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

    fun fixExistingOperationsWithEmptyBonVentKey() {
        val currentBonVentKey = zAppComptRepositoryComposable.currentAppCompt?.onVentM8BonVentKey
        if (currentBonVentKey.isNullOrEmpty()) return

        val operationsToFix = datasValue.filter { it.parentM8BonVentKeyId.isEmpty() }
        operationsToFix.forEach { operation ->
            val fixedOperation = operation.copy(
                parentM8BonVentKeyId = currentBonVentKey,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            addOrUpdateData(fixedOperation)
        }
    }

    fun acheterUneCouleur(
        zCompt: Z_AppCompt,
        relatedVentOperation: M3CouleurProduitInfos,
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
                // Error handling
            }
        }
    }

    private fun createSafeCouleurVentOperation(
        relatedCouleur: M3CouleurProduitInfos,
        zCompt: Z_AppCompt,
        quantity: Int
    ): M10OperationVentCouleur {
        return M10OperationVentCouleur(
            parentM3CouleurProduitInfosKeyID = relatedCouleur.key,
            parentHVentPeriodKeyId = zCompt.current_OnVent_M14VentPeriode_KeyID,
            parentM8BonVentKeyId = zCompt.onVentM8BonVentKey,
            parentM1ProduitInfosKeyId = relatedCouleur.parentBProduitInfosKeyID,
            parentProduitInfosOldId = relatedCouleur.parentBProduitOldID,
            parentBProduitNomDebug = relatedCouleur.parentId1ProduitInfosDebugName,
            quantityAchete = quantity,
            etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
            type = M10OperationVentCouleur.Type.CommandeDeLui,
        )
    }

    fun delete(data: M10OperationVentCouleur) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                ancienRepo.delete(data)
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun getTestDate(): List<M10OperationVentCouleur> {
        return emptyList()
    }

    companion object {
        private const val TAG = "ColorOperation"
        fun String?.findData(repo: Repo10OperationVentCouleur) =
            repo.datasValue.find { it.keyID == this }
    }
}

@Entity
data class M10OperationVentCouleur(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------Forging Keys----------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parentHVentPeriodKeyId: String = "null",
    var parentEVentPeriodDebugName: String = "null",
    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
    var parentM8BonVentKeyId: String = "null",
    val parentM8BonVentDebugInfos: String = "null",
    //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM1ProduitInfosKeyId: String = "null",
    val parentM1ProduitDebugInfos: String = "null",
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM3CouleurProduitInfosKeyID: String = "null",
    val parentM3CouleurProduitDebugInfos: String = "null",
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM13TarificationKeyID: String = "null",
    var parentM13TarificationDebugInfos: String = "null",
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,

    var parentBProduitNomDebug: String = "",
    var parentProduitInfosOldId: Long = 0,
    var parentEPeriodVentStartDate: Long = 0,

    //Mutable
    var quantityAchete: Int = 0,
    var provisoireMonPrix: Double = 0.0,
    var etateDelivery: EtateDelivery = EtateDelivery.Trouve,
    var typeTarificationEnumT2: M13TarificationInfos.TypeChoisi = M13TarificationInfos.TypeChoisi.DefiniParGerant2,

    var parentZAppComptID: String = "",
    var parentClientInfosKeyID: String = "",
    var parentClientName: String = "",
    var type: Type = Type.CommandeDeLui,
    var achatParentBsonIDOld: String = "",
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val parentDebugInfosID9AppCompt: String = "",
    val parentDebugInfosID7VentPeriod: String = "",
) {
    fun getDebugInfos(): String {
       return "$keyID =$parentM1ProduitDebugInfos / $parentM1ProduitInfosKeyId"
    }

    enum class EtateDelivery {
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
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Datas10OperationVentCouleur")


        fun isSame(
            ancien: M10OperationVentCouleur,
            newData: M10OperationVentCouleur
        ): Boolean {
            val delimiterExistence =
                ancien.keyID == newData.keyID

            return delimiterExistence
        }
    }
}
