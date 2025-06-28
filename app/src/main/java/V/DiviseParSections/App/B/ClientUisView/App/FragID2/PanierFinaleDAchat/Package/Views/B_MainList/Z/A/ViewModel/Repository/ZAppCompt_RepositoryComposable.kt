package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ASetterCentral.Companion.genereUnPushKeyFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import android.os.Build
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
import java.util.Objects

@Stable
class ZAppCompt_RepositoryComposable(
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val ouvertData by derivedStateOf {
        datasValue.firstOrNull { it.bsonObjectId == "b1" }
    }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }


    fun addOrUpdateData(data: Z_AppCompt) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.isSameEntity(dataUpdate) }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = if (existingIndex >= 0) {
                    datasValue.toMutableList().apply {
                        this[existingIndex] = this[existingIndex].copy(
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                    }
                } else datasValue + dataUpdate
            }
        }
        ancienRepo.addOrUpdatedDataBase(existingIndex, dataUpdate)
    }
}


@Entity
data class Z_AppCompt(
    @PrimaryKey
    var bsonObjectId: String = getPushFireBase(ref),
    var keyID: String = getPushFireBase(ref),

    var id: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // Section InfosDeBase
    var nom: String = "",
    var nomMutable: String = "",

    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,

    // Section StatuesMutable
    // Section Options Personnel
    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "",

    var itsProductionModePourCeCompt: Boolean = false,
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,
    var hideAppScreen: Boolean = false,
    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    // Section Centralization Valeurs Pour Injection add TOu modules

    // Section Paramaters App telephone
    var mainInitDataBaseProgressEtate: Float = 0f,

    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",
    var ouvertProduitOnVentNom: String = "",

    //---------------------------------------------------Vent Createur--------------------------------------------------------------
    //Section Parent Period Vent
    var onVentHPeriodVentKeyId: String = getPushFireBase(ref),
    var onVentHPeriodVentDebugNameKey: String ="",
    var ouvertHPeriodVentCreationTimestamp: Long = System.currentTimeMillis(),
    var ouvertHPeriodVentTimestamp: Long = creatTimeTampDepuitStr("Juin-24 08:00 AM"),

    //Section Parent Transaction
    var onVentGBonVentKeyId: String = "",
    var onVentGBonVentDebugNameKey: String = "",

    var onVentFClientKeyID: String = "",
    var onVentFClientDebugNameKey: String = "",
    var onVentFClientAncienId: Long = 0L,
) {
    init {
        if (nomMutable.isEmpty()) {
            nomMutable = getInitCreationName()
        }
    }

    fun getInitCreationName(): String {
        return nom.ifEmpty { "DefaultCompt" }
    }


    companion object {
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

                // Create Calendar and set values
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

        fun generePushKey()=genereUnPushKeyFireBase(ref)
    }

    override fun equals(other: Any?) =
        this === other || (other is Z_AppCompt && isSameEntity(other))

    fun isSameEntity(other: Z_AppCompt) =
        bsonObjectId == other.bsonObjectId
                && nom == other.nom

    override fun hashCode() = Objects.hash(
        bsonObjectId,
        nom,
    )

}
