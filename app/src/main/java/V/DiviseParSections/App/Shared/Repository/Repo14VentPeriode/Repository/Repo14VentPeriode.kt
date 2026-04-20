package V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository

import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.DataBaseInitFactory_14VentPeriode
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
class Repo14VentPeriode(
    val context: Context,
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
                    Toast.makeText(context, "Data refreshed successfully", Toast.LENGTH_SHORT)
                        .show()
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

    fun add_New(data: M14VentPeriode) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepoUpsert(dataUpdate)
    }

    fun update_If_Exist(data: M14VentPeriode) {
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

        ancienRepoUpsert(updatedItem)
    }
}

