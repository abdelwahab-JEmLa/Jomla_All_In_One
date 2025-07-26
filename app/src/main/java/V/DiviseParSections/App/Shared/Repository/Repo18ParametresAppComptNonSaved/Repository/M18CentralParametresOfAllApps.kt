package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Repo18CentralParametresOfAllApps
    (appDataBase: AppDatabase) {
    val repoTAG = "Repo18CentralParametresOfAllApps"

    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val refRepo = M18CentralParametresOfAllApps.ref
    private val dao = appDataBase.M18CentralParametresOfAllAppsDao()

    private val _data = mutableStateOf<M18CentralParametresOfAllApps?>(null)
    val dataValue by derivedStateOf { _data.value }

    init {
        repoScope.launch {
            val roomData = dao.getAll()
            if (roomData != null) {
                _data.value = roomData
            } else {
                setupFirebaseGet()
            }
        }
    }

    private fun setupFirebaseGet() {
        refRepo.get().addOnSuccessListener { snapshot ->
            val data = snapshot.getValue(M18CentralParametresOfAllApps::class.java)

            if (data?.au_Lence_Set_Compt_Ac_KeyId.isNullOrEmpty()) {
                repoScope.launch {
                    _data.value = dao.getAll()
                }
            } else {
                _data.value = data
                repoScope.launch {
                    if (data != null) {
                        dao.upsert(data)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            repoScope.launch {
                _data.value = dao.getAll()
            }
        }
    }
}

@Entity
data class M18CentralParametresOfAllApps(
    @PrimaryKey
    val keyId: String = "M18CentralParametresOfAllApps",
    val itsDevMode: Boolean = true,
    val abdelwahabCompt_KeyId: String = "-OV9dYujH9cA3yEx8AYT",
    val abdelwahabCompt_KeyId_DPL: String = "-OV9edQZecDczbx-ndPl",
    val abdelmomen_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s4",

    val au_Lence_Set_Compt_Ac_KeyId: String = "",

    val activeWindowsSearchProduit: Boolean = false,
    val devStartUpScree: String = Screen.FacadePresentoireProduits.route,
    var enablePerformAutoClickImageDisplayer: Boolean = false,
    val isControleFabVisible: Boolean = false,
) {
    companion object {
        val ref = RepositorysMainGetter.centralRef
            .child(
                "Datas" +
                        "18CentralParametresOfAllApps"
            )

        fun get_Default(): M18CentralParametresOfAllApps {
            return M18CentralParametresOfAllApps()
        }
    }
}

@Dao
interface M18CentralParametresOfAllAppsDao {

    @Query("SELECT COUNT(*) FROM M18CentralParametresOfAllApps")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M18CentralParametresOfAllApps WHERE keyId = 'M18CentralParametresOfAllApps'")
    suspend fun getAll(): M18CentralParametresOfAllApps?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M18CentralParametresOfAllApps): Long

    @Update
    suspend fun update(tarification: M18CentralParametresOfAllApps)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M18CentralParametresOfAllApps): Long

    @Query("SELECT COUNT(*) FROM M18CentralParametresOfAllApps")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M18CentralParametresOfAllApps)

    @Query("DELETE FROM M18CentralParametresOfAllApps")
    suspend fun deleteAll()
}
