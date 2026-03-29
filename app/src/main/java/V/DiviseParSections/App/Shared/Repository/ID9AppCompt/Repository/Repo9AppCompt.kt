package V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository

import Z_CodePartageEntreApps.DataBase.Repo18CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
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
class Repo9AppCompt(
    private val context: Context,
    private val ancienRepo: DataBaseInit_Z_AppCompt,
    private val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<M09AppCompt>>(emptyList())
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

    fun addNew(data: M09AppCompt) {
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

    fun update(data: M09AppCompt) {
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

    fun upsert(data: M09AppCompt) {
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

