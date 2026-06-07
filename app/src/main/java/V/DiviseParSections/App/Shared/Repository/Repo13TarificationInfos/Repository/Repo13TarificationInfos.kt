package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo13TarificationInfos(
    val dataBaseCreationFactory: DataBaseCreationFactory13TarificationInfos,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M13TarificationInfos>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }         //<--
    var on_update_M13TarificationInfos_par_ecriture: ((M13TarificationInfos) -> Unit)? = null


    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { newData ->
                _datas.value = newData
                // Clean up duplicates after data is loaded
                if (newData.isNotEmpty() && M00CentralParametresOfAllApps().au_Lence_Diminue_DatasFB) {
                    cleanupDuplicateTariffs(this@Repo13TarificationInfos, newData)
                }

                M00CentralParametresOfAllApps().time_tamp_all_tariffs.ifTrue {
                    updateTariffsWithZeroTimestamps(newData)
                }
            }
        }
    }

    private fun updateTariffsWithZeroTimestamps(tariffs: List<M13TarificationInfos>) {
        repoScope.launch {
            try {
                val currentTimestamp = System.currentTimeMillis()

                // Filter tariffs that need timestamp updates
                val tariffsToUpdate = tariffs.filter { it.creationTimestamps == 0L }

                if (tariffsToUpdate.isNotEmpty()) {
                    // Update each tariff with current timestamp
                    tariffsToUpdate.forEach { tariff ->
                        val updatedTariff = tariff.copy(
                            creationTimestamps = currentTimestamp,
                            dernierTimeTampsSynchronisationAvecFireBase = currentTimestamp
                        )

                        // Save to database and Firebase
                        dataBaseCreationFactory.set(updatedTariff)
                    }

                    // Update local state
                    withContext(Dispatchers.Main.immediate) {
                        _datas.value = _datas.value.map { tariff ->
                            if (tariff.creationTimestamps == 0L) {
                                tariff.copy(
                                    creationTimestamps = currentTimestamp,
                                    dernierTimeTampsSynchronisationAvecFireBase = currentTimestamp
                                )
                            } else {
                                tariff
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error if needed
            }
        }
    }
    fun upsert(data: M13TarificationInfos) {
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
        on_update_M13TarificationInfos_par_ecriture?.invoke(dataUpdate)
    }


    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.fetchDataFromFirebase()

                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

            } catch (e: Exception) {
            }
        }
    }


    fun add(data: M13TarificationInfos) {
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
        on_update_M13TarificationInfos_par_ecriture?.invoke(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M13TarificationInfos) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M13TarificationInfos) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }
}

