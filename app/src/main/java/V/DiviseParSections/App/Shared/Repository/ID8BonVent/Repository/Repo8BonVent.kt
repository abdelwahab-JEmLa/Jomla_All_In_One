package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.cleanupOldBonVents
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo8BonVent(
    private val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_8BonVent,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M8BonVent>>(emptyList())
    val datasValue by derivedStateOf { _datas.value.sortedBy { it.creationTimestamps } }

    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { newData ->
                _datas.value = newData

                if (newData.isNotEmpty() && M00CentralParametresOfAllApps().au_Lence_Dimininue_Datas_M8BonVents) {
                    cleanupOldBonVents(this@Repo8BonVent, newData)
                }
            }
        }
    }

    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()
                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.onLoadFromFireBase()
                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Data refreshed successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Failed to refresh data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun upsert(data: M8BonVent) {
        val dataUpdate = data.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    fun add(data: M8BonVent) {
        val dataUpdate = data.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M8BonVent) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M8BonVent) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    fun addNew(data: M8BonVent) {
        val dataUpdate = data.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        dataBaseCreationFactory.set(dataUpdate)
    }
}

