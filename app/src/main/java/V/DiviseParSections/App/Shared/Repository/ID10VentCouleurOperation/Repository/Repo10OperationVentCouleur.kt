package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
        datasValue.filter { it.parent_M8BonVent_KeyId == targetKey }
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

        val operationsToFix = datasValue.filter { it.parent_M8BonVent_KeyId.isEmpty() }
        operationsToFix.forEach { operation ->
            val fixedOperation = operation.copy(
                parent_M8BonVent_KeyId = currentBonVentKey,
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
            parent_M8BonVent_KeyId = zCompt.onVentM8BonVentKey,
            parent_M1Produit_KeyId = relatedCouleur.parentBProduitInfosKeyID,
            parent_M1Produit_DebugInfos = relatedCouleur.parentId1ProduitInfosDebugName,
            parentProduitInfosOldId = relatedCouleur.parentBProduitOldID,
            parent_M3CouleurProduit_KeyID = relatedCouleur.keyID,
            etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
            quantity = quantity,
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

    //---------------------------------LinkedVent----------------------------------------------------------------------------------------------------------------------------------
    var its_Linked_To_Autre_Vent_Si_NonDispo: Boolean = false,
    val linked_To_M10OperationVent_KeyID: String = "null",
    val linked_To_M10OperationVent_DebugInfos: String = "null",

    //---------------------------------M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "null",
    val parent_M9AppCompt_DebugInfos: String = "null",
    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    var parentEPeriodVentStartDate: Long = 0,
    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
    var parent_M8BonVent_KeyId: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",
    //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parent_M1Produit_KeyId: String = "null",
    val parent_M1Produit_DebugInfos: String = "null",
    var parentProduitInfosOldId: Long = 0,
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parent_M3CouleurProduit_KeyID: String = "null",
    val parent_M3CouleurProduit_DebugInfos: String = "null",
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM13TarificationKeyID: String = "null",
    var parentM13TarificationDebugInfos: String = "null",
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,

    //Mutable
    var provisoireMonPrix: Double = 0.0,
    var etateDelivery: EtateDelivery = EtateDelivery.Trouve,
    var typeTarificationEnumT2: M13TarificationInfos.TypeChoisi = M13TarificationInfos.TypeChoisi.DefiniParGerant,

    var parentClientInfosKeyID: String = "",
    var parentClientName: String = "",
    var type: Type = Type.CommandeDeLui,
    var achatParentBsonIDOld: String = "",

    val quantite_Boit_Par_Carton: Int = 10,
    var quantity: Int = 0,
    var setIN_Vent_Its_Quantity_Represent: SetIN_Vent_Its_Quantity_Represent =
        SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit,
    var affiche_Unite_Au_Printing: Boolean = true,
) {
    fun getDebugInfos(): String {
        return buildString {
            append("[")
            append("10Vent")
            append("{${keyID.takeLast(4).uppercase()}}\n")
            append(" To ")
            append(parent_M1Produit_DebugInfos)
            append("]")
        }
    }
    enum class SetIN_Vent_Its_Quantity_Represent {
        quantity_Par_Boit,
        quantity_Par_Carton;

        fun toggle(): SetIN_Vent_Its_Quantity_Represent {
            return when (this) {
                quantity_Par_Boit -> quantity_Par_Carton
                quantity_Par_Carton -> quantity_Par_Boit
            }
        }
    }

    fun get_Quantity_Apre_Passe_Au_SetIN_Vent_Its_Quantity_Represent(): Int {
        return if (setIN_Vent_Its_Quantity_Represent ==
            SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        )
            quantity / quantite_Boit_Par_Carton
        else
            quantity
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
        fun get_default(
            onVent_M8BonVent: M8BonVent?,
            m3CouleurProduit: M3CouleurProduitInfos?
        ): M10OperationVentCouleur {
            return M10OperationVentCouleur(
                parent_M9AppCompt_KeyID = onVent_M8BonVent?.parent_M9AppCompt_KeyID ?: "null",
                parent_M9AppCompt_DebugInfos = onVent_M8BonVent?.parent_M9AppCompt_DebugInfos ?: "null",

                parent_M14VentPeriod_KeyId = onVent_M8BonVent?.parent_M14VentPeriod_KeyId ?: "null",
                parent_M14VentPeriod_DebugInfos = onVent_M8BonVent?.parent_M14VentPeriod_DebugInfos ?: "null",

                parent_M8BonVent_KeyId = onVent_M8BonVent?.keyID ?: "null",
                parent_M8BonVent_DebugInfos = onVent_M8BonVent?.get_DebugInfos() ?: "null",

                parent_M1Produit_KeyId = m3CouleurProduit?.parentBProduitInfosKeyID ?: "null",
                parent_M1Produit_DebugInfos = m3CouleurProduit?.parentBProduitInfosKeyID ?: "null",

                parent_M3CouleurProduit_KeyID = m3CouleurProduit?.keyID ?: "null",
                parent_M3CouleurProduit_DebugInfos = m3CouleurProduit?.get_DebugsInfos() ?: "null",
            )
        }

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
