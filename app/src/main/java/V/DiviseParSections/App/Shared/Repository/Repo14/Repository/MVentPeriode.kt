package V.DiviseParSections.App.Shared.Repository.Repo14.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.MainRepositorysSetterFacade
import V.DiviseParSections.App.Shared.Repository.Repo14.Repository.Base.Factory.DataBaseInitFactory_14VentPeriode
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo8BonVent(
    val dataBaseCreationFactory: DataBaseInitFactory_14VentPeriode,
) {
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M14VentPeriode>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun upsert(data: M14VentPeriode) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    if (existingIndex >= 0) {
                        this[existingIndex] = dataUpdate
                    } else {
                        add(dataUpdate)
                    }
                }
            }
        }
        ancienRepoUpsert(dataUpdate)
    }

    private fun ancienRepoUpsert(dataUpdate: M14VentPeriode) {
        dataBaseCreationFactory.upsert(dataUpdate)
    }

    fun delete(data: M14VentPeriode) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }
}

@Entity
data class M14VentPeriode(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------Forging Keys.Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M9AppCompt_KeyID: String = "",
    var parent_M9AppCompt_DebugInfos: String = "",
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.SoquetteNonDefinie,
) {
    fun get_DebugInfos(): String {
        return buildString {
            append(parent_M9AppCompt_DebugInfos)
            append(" ")
            append(keyID.takeLast(4).uppercase())
        }
    }

    enum class EtateActuellementEst {
        SoquetteNonDefinie,
        CONFIRME,
    }

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DatasM14VentPeriode"
        )

        fun generePushKey() = MainRepositorysSetterFacade.genereUnPushKeyFireBase(ref)
    }
}
