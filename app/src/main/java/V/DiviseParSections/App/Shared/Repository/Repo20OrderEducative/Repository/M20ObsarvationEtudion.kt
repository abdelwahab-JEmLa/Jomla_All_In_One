package V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant.Takiyim
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase20.Factory.DataBaseInitFactory_M20ObsarvationEtudion
import android.content.Context
import android.widget.Toast
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
class Repo20ObsarvationEtudion(
    private val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_M20ObsarvationEtudion,
) {
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M20ObsarvationEtudion>>(emptyList())
    val datasValue by derivedStateOf { _datas.value.sortedBy { it.creationTimestamps } }

    private val _filter_query = mutableStateOf<Utilisateur?>(null)

    val filtered_datasValue by derivedStateOf {
        val currentFilter = _filter_query.value
        if (currentFilter == null || currentFilter == Utilisateur.Admin) {
            _datas.value
        } else {
            val params = M18CentralParametresOfAllApps()
            val targetKeyId = when (currentFilter) {
                Utilisateur.Abdelwahab_Osstad -> params.abdelwahabTravailleChezGros_KeyId
                Utilisateur.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
                Utilisateur.Abdelmoumen -> params.abdelmomen_Compt_KeyId
                Utilisateur.Walid -> params.walid_Compt_KeyId
                Utilisateur.Admin -> return@derivedStateOf _datas.value
            }
            _datas.value.filter { it.parent_ousstad_key == targetKeyId }
        }
    }

    // Get all unique moulahadat sorted by teacher and recency
    fun getSortedMoulahadatForTeacher(teacherKeyID: String): List<String> {
        // Get moulahadat from this teacher's observations (most recent first)
        val teacherMoulahadat = _datas.value
            .filter { it.parent_ousstad_key == teacherKeyID }
            .sortedByDescending { it.creationTimestamps }
            .flatMap { it.getMoulahadatList() }
            .distinct()

        // Get moulahadat from other teachers
        val otherMoulahadat = _datas.value
            .filter { it.parent_ousstad_key != teacherKeyID }
            .flatMap { it.getMoulahadatList() }
            .distinct()
            .filter { it !in teacherMoulahadat }
            .sorted()

        // Teacher's moulahadat first (most recent), then others (alphabetically)
        return teacherMoulahadat + otherMoulahadat
    }

    // Get all unique moulahadat from all observations
    val allUniqueMoulahadat by derivedStateOf {
        _datas.value
            .flatMap { it.getMoulahadatList() }
            .distinct()
            .sorted()
    }

    // Update all observations that contain a specific moulahada
    fun updateMoulahadaGlobally(oldMoulahada: String, newMoulahada: String) {
        if (oldMoulahada.isBlank() || newMoulahada.isBlank()) return

        repoScope.launch {
            val observationsToUpdate = _datas.value.filter { obs ->
                obs.hasMoulahada(oldMoulahada)
            }

            observationsToUpdate.forEach { obs ->
                val updatedList = obs.getMoulahadatList().map {
                    if (it == oldMoulahada) newMoulahada else it
                }
                val updatedObs = obs.copy(
                    moulahadat_takyim_li_islahiha = updatedList.joinToString(","),
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )

                // Update in database
                dataBaseCreationFactory.set(updatedObs)

                // Update in local list
                withContext(Dispatchers.Main.immediate) {
                    val index = _datas.value.indexOfFirst { it.keyID == updatedObs.keyID }
                    if (index >= 0) {
                        _datas.value = _datas.value.toMutableList().apply {
                            this[index] = updatedObs
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "تم تحديث ${observationsToUpdate.size} سجل",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun setFilter(utilisateur: Utilisateur?) {
        _filter_query.value = utilisateur
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

    fun upsert(data: M20ObsarvationEtudion) {
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

    fun add(data: M20ObsarvationEtudion) {
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

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M20ObsarvationEtudion) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M20ObsarvationEtudion) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    fun addNew(data: M20ObsarvationEtudion) {
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

    fun updateIfExist(data: M20ObsarvationEtudion) {
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

@Entity
data class M20ObsarvationEtudion(
    @PrimaryKey
    var keyID: String = generePushKey(),

    var type: Type = Type.Tama_Hifdoha,

    var etudiant_keyID: String = "",

    var min_soura: SOUAR = SOUAR.El_Nasse,
    var min_aya: Int = 1,
    var min_sattre: Int = 1,

    var ila_soura: SOUAR = SOUAR.El_Nasse,
    var ila_aya: Int = 1,
    var ila_sattre: Int = 1,

    var tikrar: Int = 1,
    var el3arde: Int = 1,

    var takyim: Takiyim = Takiyim.Jayid,

    // Store custom moulahadat as comma-separated string
    var moulahadat_takyim_li_islahiha: String = "",

    var parent_ousstad_key: String = M18CentralParametresOfAllApps().abdelwahabTravailleChezGros_KeyId,

    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    enum class Type {
        Raeeb,
        Tama_Hifdoha,
        Moukarrar_Itmamouhou,
        Ousstad_kama_Bil_moundat,
    }

    // Helper methods for working with moulahadat as strings
    fun getMoulahadatList(): List<String> {
        if (moulahadat_takyim_li_islahiha.isBlank()) return emptyList()
        return moulahadat_takyim_li_islahiha
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun setMoulahadatList(moulahadatList: List<String>) {
        moulahadat_takyim_li_islahiha = moulahadatList
            .filter { it.isNotBlank() }
            .joinToString(",")
    }

    fun hasMoulahada(moulahada: String): Boolean {
        return getMoulahadatList().contains(moulahada)
    }

    fun addMoulahada(moulahada: String) {
        if (moulahada.isBlank()) return
        val currentList = getMoulahadatList().toMutableList()
        if (!currentList.contains(moulahada)) {
            currentList.add(moulahada)
            setMoulahadatList(currentList)
        }
    }

    fun removeMoulahada(moulahada: String) {
        val currentList = getMoulahadatList().toMutableList()
        currentList.remove(moulahada)
        setMoulahadatList(currentList)
    }

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("DatasM20ObsarvationEtudion")

        fun generePushKey() = genereUnPushKeyFireBase(ref)

        fun get_default(): M20ObsarvationEtudion {
            return M20ObsarvationEtudion()
        }
    }
}
