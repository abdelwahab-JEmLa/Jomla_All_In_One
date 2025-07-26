package V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo9AppCompt(
    private val context: Context,
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17,
    private val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val currentAppCompt by derivedStateOf {
        datasValue.firstOrNull {
            it.keyID ==
                    (repo18CentralParametresOfAllApps.dataValue?.au_Lence_Set_Compt_Ac_KeyId ?: "")
        }
    }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addNew(data: Z_AppCompt) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepo.addOrUpdatedDataBase(-1, dataUpdate)
    }

    fun update(data: Z_AppCompt) {
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

        ancienRepo.addOrUpdatedDataBase(existingIndex, updatedItem)
    }

    fun upsert(data: Z_AppCompt) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
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

        ancienRepo.addOrUpdatedDataBase(existingIndex, dataForRepo)
    }
}

@Entity
data class Z_AppCompt(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var appDesignedPourWorkingGrossisst3Ali: Boolean = true,

    // Section InfosDeBase
    var nom: String = "",
    var autres_Noms_SepareParComma: String = "",

    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,


    // Section Options Personnel
    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "",
    var hideAppScreen: Boolean = false,
    val travailleChezGrossisst3Ali: Boolean = false,
    val its_Admin: Boolean = false,


    var itsProductionModePourCeCompt: Boolean = false,
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,

    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    // Section Centralization Valeurs Pour Injection add_New TOu modules

    // Section Paramaters App telephone


    var mainInitDataBaseProgressEtate: Float = 0f,
    //---------------------------------Centrale_Focuces_Values.----------------------------------------------------------------------------------------------------------------------------------
    val activeDialogSearchM1Produit: Boolean = false,

    //------------------------------------------------------------------------------------------------------------------------------------------------------------


    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",
    var ouvertProduitOnVentNom: String = "",

    //---------------------------------Parent.M14VentPeriode----------------------------------------------------------------------------------------------------------------------------------
    var current_OnVent_M14VentPeriode_KeyID: String = "",
    var current_OnVent_M14VentPeriode_DebugInfos: String = "",
    //---------------------------------------------------Vent Createur--------------------------------------------------------------
    var onVentM8BonVentKey: String = "",
    var onVentM8BonVentDebugInfos: String = "",
    //---------------------------------Parent.M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var onVentM1ProduitInfosKeyID: String = "",
    var onVentM1ProduitInfosDebugName: String = "",
    //---------------------------------Parent.M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var onVentM3CouleurProduitInfosKeyID: String = "null",
    val onVentM3CouleurProduitDebugInfos: String = "null",
    //------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------DialogOpner.DialogAboveAll.OutlinedSearchListProduits----------------------------------------------------------------------------------------------------------------------------------
    var dialogAboveAll_OutlinedSearchListProduits: Boolean=false,

    //---------------------------------dialogChoisireQuantityM1Produit----------------------------------------------------------------------------------------------------------------------------------
    var dialogChoisireQuantityM1ProduitInfosKeyID: String = "null",
    var dialogChoisireQuantityM1ProduitInfosDebugName: String = "null",

    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var activeFocuce_TariffPrixDifineur_M1ProduitKeyID: String = "null",
    var activeFocuceTariffPrixDifineurM1ProduitDebugInfos: String = "null",
    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var startTextSearchM1Produit: String = "",
    //------------------------------------------------------------------------------------------------------------------------------------------------

    //------------------------------------A SUPP ------------------------------------------------------------------------------------------------------------
    var KeyByParent: String = "",
    var vid: Long = 1,
) {
    fun get_DebugInfos(): String {
        return buildString {
            append("(M9=")
            append(nom)
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    fun Z_AppCompt.addStringAuNomsMutableTags(str: String): List<String> {
        val currentTags = if (autres_Noms_SepareParComma.isNotEmpty()) {
            autres_Noms_SepareParComma.split(",").map { it.trim() }
        } else {
            emptyList()
        }

        return if (currentTags.contains(str)) {
            currentTags
        } else {
            currentTags + str
        }
    }

    // Updated getList_autres_Noms_SepareParComma function in Z_AppCompt class
    fun getList_autres_Noms_SepareParComma(): List<String> {
        return if (autres_Noms_SepareParComma.isNotEmpty()) {
            autres_Noms_SepareParComma.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }

    companion object {
        const val keyModel = "ID9"
        const val keyModelValID7VentParent = "ID7"

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        fun get_Default() = Z_AppCompt()


        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )
        fun generePushKey() = genereUnPushKeyFireBase(ref)
    }
}
