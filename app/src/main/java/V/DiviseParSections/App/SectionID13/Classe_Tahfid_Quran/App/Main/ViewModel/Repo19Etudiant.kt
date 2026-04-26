package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel

import Application5.App.Repository.M19Etudiant
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repository.DataBaseInitFactory_19Etudiant
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
class Repo19Etudiant(
    private val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_19Etudiant,
) {
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M19Etudiant>>(emptyList())
    val datasValue by derivedStateOf { _datas.value.sortedBy { it.creationTimestamps } }

    // FIXED: Changed from Utilisateur to Ousstad_Tahfid
    private val _filter_query = mutableStateOf<Ousstad_Tahfid?>(null)

    val filtered_datasValue by derivedStateOf {
        val currentFilter = _filter_query.value
        if (currentFilter == null) {
            _datas.value
        } else {
            val params = M00CentralParametresOfAllApps()
            val targetKeyId = when (currentFilter) {
                Ousstad_Tahfid.Abdelwahab_Osstad -> Compts.AbdelwahabTravailleChezGros_KeyId.keyId
                Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
                Ousstad_Tahfid.Kissm_Intikali -> params.kissm_intikali_madrasa_Compt_KeyId
                Ousstad_Tahfid.Non_Defini_Actuellemen -> return@derivedStateOf _datas.value
                else -> {}
            }
            _datas.value.filter { it.parent_ousstad_key == targetKeyId }
        }
    }

    fun setFilter(ousstad: Ousstad_Tahfid?) {
        _filter_query.value = ousstad
    }

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
                    Toast.makeText(context, "Data refreshed successfully", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to refresh data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun upsert(data: M19Etudiant) {
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
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    fun add(data: M19Etudiant) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M19Etudiant) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M19Etudiant) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    fun addNew(data: M19Etudiant) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateIfExist(data: M19Etudiant) {
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

        dataBaseCreationFactory.set(updatedItem)
    }
}

