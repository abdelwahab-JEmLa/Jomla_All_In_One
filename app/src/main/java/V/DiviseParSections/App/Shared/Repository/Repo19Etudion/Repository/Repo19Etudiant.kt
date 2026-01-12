package V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory.DataBaseInitFactory_19Etudiant
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
import java.util.Calendar

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
            val params = M18CentralParametresOfAllApps()
            val targetKeyId = when (currentFilter) {
                Ousstad_Tahfid.Abdelwahab_Osstad -> params.abdelwahabTravailleChezGros_KeyId
                Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
                Ousstad_Tahfid.Non_Defini_Actuellemen -> return@derivedStateOf _datas.value
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

@Entity
data class M19Etudiant(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var nom: String = "",
    var prenom: String = "",

    var parent_ousstad_key: String = M18CentralParametresOfAllApps().abdelwahabTravailleChezGros_KeyId,

    var num_telephone_parent: String = "",
    var age: Int = 7,
    var positon_don_classe: Int = 1,

    // REMOVED: nmbr_absence_sans_justification - now calculated from observations
    var imprime_justification : Boolean = false,
    var exclue_de_l_affiche_au_classe : Boolean = false,

    var question_par_non : String = "هل يعاني ابنكم من مرض معين جزاكم الله خيرا؟",

    var dernier_Soura_Wassale_Laha: SOUAR = SOUAR.El_Nasse,
    var dernier_Soura_sater: Int = 1,

    var dernier_takyim_dabte: Takiyim = Takiyim.Jayid,
    var tikrare: Int = 1,

    var tikrare_3arde: Int = 1,

    var mokarrare_hifde: SOUAR = SOUAR.El_Nasse,
    var mokarrare_hifde_sater: Int = 1,

    var moulahada_3ala_soulouk: MoulahadaSoulouk = MoulahadaSoulouk.Rien,
    var moulahada_makouba: String = "",

    var istedrak_kadim_Akher_Soura_Wassale_Laha: SOUAR = SOUAR.El_Nasse,
    var istedrak_kadim_Moukarare: SOUAR = SOUAR.El_Nasse,
    var istedrak_kadim_Takyim_hali: Takiyim = Takiyim.Maqboul,

    var absent: Boolean = false,
    var mokarrare_hifde_mahssou_li_3idat_souer: Int = 1,

    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    enum class Takiyim(val arabicName: String) {
        Moumtaz("ممتاز"),
        Jayid_Jiddan("جيد جداً"),
        Fawk_Jayid("فوق الجيد"),
        Jayid("جيد"),
        Fawk_Makbol("فوق المقبول"),
        Maqboul("مقبول"),
        Lam_Yahfed("لم يحفظ")
    }

    enum class MoulahadaSoulouk(val arabicName: String) {
        Rien("---"),
        Moumtaz("ممتاز"),
        Jayid_Jiddan("جيد جداً"),
        Jayid("جيد"),
        Maqboul("مقبول"),
        Daeef("ضعيف")
    }

    /**
     * Calculate unjustified absences for the current month from observations
     * Counts only Raeeb observations without justification (tabrire_riyab is blank)
     */
    fun calculateUnjustifiedAbsences(
        observations: List<M20ObsarvationEtudion>
    ): Int {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return observations.count { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obs.etudiant_keyID == keyID &&
                    obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                    obs.tabrire_riyab.isBlank() && // Not justified
                    obsDate.get(Calendar.MONTH) == currentMonth &&
                    obsDate.get(Calendar.YEAR) == currentYear
        }
    }

    /**
     * Calculate total absences (justified + unjustified) for the current month
     */
    fun calculateTotalAbsences(
        observations: List<M20ObsarvationEtudion>
    ): Int {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return observations.count { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obs.etudiant_keyID == keyID &&
                    obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                    obsDate.get(Calendar.MONTH) == currentMonth &&
                    obsDate.get(Calendar.YEAR) == currentYear
        }
    }

    fun get_DebugInfos(): String {
        return buildString {
            append("Nom: $nom, ")
            append("Age: $age, ")
            append("Dernier Soura: ${dernier_Soura_Wassale_Laha.arabicName} (${dernier_Soura_sater}), ")
            append("Mokarrare: ${mokarrare_hifde.arabicName} (${mokarrare_hifde_sater})")
        }
    }

    companion object {
        const val keyModel = "ID19"

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("Datas19Etudiant")

        fun generePushKey() = genereUnPushKeyFireBase(ref)

        fun get_default2(): M19Etudiant {
            return M19Etudiant()
        }

        fun get_default(): M19Etudiant {
            return M19Etudiant()
        }
    }
}
