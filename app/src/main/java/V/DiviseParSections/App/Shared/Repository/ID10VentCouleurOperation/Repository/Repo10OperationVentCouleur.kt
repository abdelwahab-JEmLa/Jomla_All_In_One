package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import android.content.Context
import android.widget.Toast
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
    val context: Context,
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<M10OperationVentCouleur>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val onVentFilteredDatas by derivedStateOf {
        val targetKey = zAppComptRepositoryComposable.currentAppCompt?.onVentM8BonVentKey
        datasValue.filter { it.parentM8BonVentKeyId == targetKey }
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
            parent_M14VentPeriod_KeyId = zCompt.current_OnVent_M14VentPeriode_KeyID,
            parentM8BonVentKeyId = zCompt.onVentM8BonVentKey,
            parentM1ProduitInfosKeyId = relatedCouleur.parentBProduitInfosKeyID,
            parentM1ProduitDebugInfos = relatedCouleur.parentId1ProduitInfosDebugName,
            parentProduitInfosOldId = relatedCouleur.parentBProduitOldID,
            parentM3CouleurProduitInfosKeyID = relatedCouleur.keyID,
            etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
            quantity_Par_Boit = quantity,
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
    //---------------------------------Forging Keys----------------------------------------------------------------------------------------------------------------------------------

    fun add_New(data: M10OperationVentCouleur) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepo.addOrUpdatedAncienRepo(-1, data)
    }

    fun update_If_Exist(data: M10OperationVentCouleur) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
    }
}
@Entity
data class M10OperationVentCouleur(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------Forging Keys----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "null",
    val parent_M9AppCompt_DebugInfos: String = "null",
    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    var parentEPeriodVentStartDate: Long = 0,
    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
    var parentM8BonVentKeyId: String = "null",
    val parentM8BonVentDebugInfos: String = "null",
    //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM1ProduitInfosKeyId: String = "null",
    val parentM1ProduitDebugInfos: String = "null",
    var parentProduitInfosOldId: Long = 0,
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM3CouleurProduitInfosKeyID: String = "null",
    val parentM3CouleurProduitDebugInfos: String = "null",
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM13TarificationKeyID: String = "null",
    var parentM13TarificationDebugInfos: String = "null",
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,

    //Mutable
    var provisoireMonPrix: Double = 0.0,
    var etateDelivery: EtateDelivery = EtateDelivery.Trouve,
    var typeTarificationEnumT2: M13TarificationInfos.TypeChoisi = M13TarificationInfos.TypeChoisi.DefiniParGerant2,

    var parentClientInfosKeyID: String = "",
    var parentClientName: String = "",
    var type: Type = Type.CommandeDeLui,
    var achatParentBsonIDOld: String = "",

    var quantity_Par_Boit: Int = 0,
    val quantity_Par_Carton: Int = 1,
    var setIN_VentQuantity_Actuellement_Va_Remplire: SetIN_VentQuantity_Actuellement_Va_Remplire = SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Boit,
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
) {
    enum class SetIN_VentQuantity_Actuellement_Va_Remplire {
        quantity_Par_Boit,
        quantity_Par_Carton;

        fun toggle(): SetIN_VentQuantity_Actuellement_Va_Remplire {
            return when (this) {
                quantity_Par_Boit -> quantity_Par_Carton
                quantity_Par_Carton -> quantity_Par_Boit
            }
        }

        // Additional utility function to get display name
        fun getDisplayName(): String {
            return when (this) {
                quantity_Par_Boit -> "Par Boîte"
                quantity_Par_Carton -> "Par Carton"
            }
        }

        // Additional utility function to get multiplier
        fun getMultiplier(): Int {
            return when (this) {
                quantity_Par_Boit -> 1
                quantity_Par_Carton -> 24 // Assuming 24 units per carton, adjust as needed
            }
        }
    }
    fun getDebugInfos(): String {
        return buildString {
            append("KeyID: $keyID\n")
            append("Parent Product: $parentM1ProduitDebugInfos\n")
            append("Quantity: $quantity_Par_Boit\n")
            append("Unit Type: ${setIN_VentQuantity_Actuellement_Va_Remplire.getDisplayName()}\n")
            append("State: $etateActuellementEst\n")
            append("Delivery: $etateDelivery\n")
            append("Type: $type")
        }
    }

    fun toggleQuantityUnit(): M10OperationVentCouleur {
        val newQuantityUnit = setIN_VentQuantity_Actuellement_Va_Remplire.toggle()

        // Convert quantity when toggling units
        val newQuantity = when (newQuantityUnit) {
            SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Carton -> {
                // Convert from boîte to carton
                if (quantity_Par_Boit > 0) quantity_Par_Boit / newQuantityUnit.getMultiplier() else 0
            }
            SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Boit -> {
                // Convert from carton to boîte
                quantity_Par_Boit * setIN_VentQuantity_Actuellement_Va_Remplire.getMultiplier()
            }
        }

        return this.copy(
            setIN_VentQuantity_Actuellement_Va_Remplire = newQuantityUnit,
            quantity_Par_Boit = newQuantity,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    // Helper function to get effective quantity based on unit type
    fun getEffectiveQuantity(): Int {
        return when (setIN_VentQuantity_Actuellement_Va_Remplire) {
            SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Boit -> quantity_Par_Boit
            SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Carton -> quantity_Par_Boit * setIN_VentQuantity_Actuellement_Va_Remplire.getMultiplier()
        }
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
            return ancien.keyID == newData.keyID
        }
    }
}
