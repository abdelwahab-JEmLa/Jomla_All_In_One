package V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.Set.Companion.genereUnPushKeyFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import android.content.Context
import android.os.Build
import android.util.Log
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
import java.util.Calendar

@Stable
class Repo9AppCompt(
    private val context: Context,
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val
            datasValue by derivedStateOf { _datas.value }

    val currentAppCompt by derivedStateOf {
        datasValue.firstOrNull { it.keyID == ParametresAppComptNonSaved().currentActiveFocucedM9AppComptKeyID }
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

    fun updateIfExist(data: Z_AppCompt) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT).show()
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
    var keyID: String = getPushFireBase(ref),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var appDesignedPourWorkingGrossisst3Ali: Boolean = true,

    // Section InfosDeBase
    var nom: String = "",
    var nomsMutableTags: String = "",

    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,


    // Section Options Personnel
    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "",

    var itsProductionModePourCeCompt: Boolean = false,
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,
    var hideAppScreen: Boolean = false,
    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    // Section Centralization Valeurs Pour Injection addNew TOu modules

    // Section Paramaters App telephone
    val activeDialogSearchM1Produit: Boolean = false,

    val travailleChezGrossisst3Ali: Boolean = false,

    var mainInitDataBaseProgressEtate: Float = 0f,

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

    //---------------------------------Dialog Opner Vars ----------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var dialogChoisireQuantityM1ProduitInfosKeyID: String = "null",
    var dialogChoisireQuantityM1ProduitInfosDebugName: String = "null",
    //------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var activeFocuce_TariffPrixDifineur_M1ProduitKeyID: String = "null",
    var activeFocuseTariffPrixDifineurM1ProduitDebugInfos: String = "null",
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
        val currentTags = if (nomsMutableTags.isNotEmpty()) {
            nomsMutableTags.split(",").map { it.trim() }
        } else {
            emptyList()
        }

        return if (currentTags.contains(str)) {
            currentTags
        } else {
            currentTags + str
        }
    }

    // Updated getListNomsMutableTags function in Z_AppCompt class
    fun getListNomsMutableTags(): List<String> {
        return if (nomsMutableTags.isNotEmpty()) {
            nomsMutableTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }

    companion object {
        const val keyModel = "ID9"
        const val keyModelValID7VentParent = "ID7"

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        fun creatTimeTampDepuitStr(dateString: String): Long {
            return try {
                // Parse the French month and format
                val parts = dateString.split(" ")
                if (parts.size < 3) return System.currentTimeMillis()

                val monthYear = parts[0] // "Juin-24"
                val time = parts[1] // "08:00"
                val amPm = parts[2] // "AM"

                val monthYearParts = monthYear.split("-")
                if (monthYearParts.size != 2) return System.currentTimeMillis()

                val monthStr = monthYearParts[0]
                val yearStr = "20${monthYearParts[1]}" // Convert "24" to "2024"

                // French month mapping
                val monthMap = mapOf(
                    "Janvier" to 0, "Février" to 1, "Mars" to 2, "Avril" to 3,
                    "Mai" to 4, "Juin" to 5, "Juillet" to 6, "Août" to 7,
                    "Septembre" to 8, "Octobre" to 9, "Novembre" to 10, "Décembre" to 11
                )

                val month = monthMap[monthStr] ?: return System.currentTimeMillis()
                val year = yearStr.toIntOrNull() ?: return System.currentTimeMillis()

                // Parse time
                val timeParts = time.split(":")
                if (timeParts.size != 2) return System.currentTimeMillis()

                var hour = timeParts[0].toIntOrNull() ?: return System.currentTimeMillis()
                val minute = timeParts[1].toIntOrNull() ?: return System.currentTimeMillis()

                // Convert to 24-hour format
                if (amPm.uppercase() == "PM" && hour != 12) {
                    hour += 12
                } else if (amPm.uppercase() == "AM" && hour == 12) {
                    hour = 0
                }

                // Create Calendar and upsert values
                val calendar = Calendar.getInstance()
                calendar.set(year, month, 1, hour, minute, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                calendar.timeInMillis
            } catch (e: Exception) {
                // Return current time as fallback
                System.currentTimeMillis()
            }
        }

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )

        fun generePushKey() = genereUnPushKeyFireBase(ref)

        fun Z_AppCompt?.logDebugIt(nomVale: String = "") {
            Log.d(
                "Z_AppCompt",
                infos(nomVale)
            )
        }

        private fun Z_AppCompt?.infos(
            nomVale: String
        ) = nomVale + if (this != null) {
            "${keyID}\n ${nom}\n "
        } else {
            "Z_AppCompt is null"
        }
    }
}
