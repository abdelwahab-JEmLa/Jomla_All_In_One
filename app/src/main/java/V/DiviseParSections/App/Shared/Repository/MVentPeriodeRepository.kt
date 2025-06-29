package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.ASetterCentral.Companion.genereUnPushKeyFireBase
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Stable
class MVentPeriodeRepository(
    private val appDatabase: AppDatabase,
    ancientProtoRepo: GroupeRepositorysProtoAvJuin3Model,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    val ancienRepoP1 = ancientProtoRepo.repositoryMVentPeriode

    val dao = appDatabase.MVentPeriodeDao()
    val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _datas = mutableStateOf<List<MVentPeriode>>(emptyList())

    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dao.getAllFlow().collect { data ->
                withContext(Dispatchers.Main) {
                    _datas.value = data
                }
            }
        }
    }


    fun addOrUpdateData(data: MVentPeriode) {
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
        ancienRepoAddOrUpdatedDataBase(existingIndex, dataForRepo)
    }

    fun ancienRepoAddOrUpdatedDataBase(existingIndex: Int, dataForRepo: MVentPeriode): Unit {
        ancienRepoP1.addOrUpdatedDataBase(existingIndex, dataForRepo)
    }

    companion object {
        private const val TAG = "MVentPeriode"
    }
}

@Entity
data class MVentPeriode(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    val vid: Long = 0,


    // Section InfosDeBase
    var vendeur_ParentVID: Long = 0L,
    var startDateInString: String = "",
    var heurDebutInString: String = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date()),
    var endDateInString: String = "",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME,
) {

    enum class EtateActuellementEst {
        ENTRE_MAIS_PAS_CONFIRME,
        CONFIRME,
        NA_PAS_COMMANDE,
    }


    companion object {

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/MVentPeriode"
        )

        fun generePushKey() = genereUnPushKeyFireBase(ref)

        fun MVentPeriode?.logDebugIt(nomVale: String = "") {
            Log.d(
                "MVentPeriode",
                infos(nomVale)
            )
        }

        private fun MVentPeriode?.infos(
            nomVale: String
        ) = nomVale + if (this != null) {
            //   "${keyID}\n ${nom}\n ${onVentFClientDebugNameKey}\n"
        } else {
            "MVentPeriode is null"
        }
    }
}
