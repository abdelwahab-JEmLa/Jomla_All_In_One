package V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository

import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.DataBaseInitFactory_15Grossist
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
class Repo15Grossist(
    val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_15Grossist,
) {
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M15Grossist>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun add_New(data: M15Grossist) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, data)
    }

    fun update_If_Exist(data: M15Grossist) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
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

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, data)
    }

    fun delete(data: M15Grossist) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteMulti(datas: List<M15Grossist>?= datasValue) {
        repoScope.launch {
            val keyIDsToDelete = datas!!.map { it.keyID }.toSet()
            _datas.value = datasValue.filter { it.keyID !in keyIDsToDelete }
            datas.forEach { item ->
                dataBaseCreationFactory.delete(item)
            }
        }
    }

}

