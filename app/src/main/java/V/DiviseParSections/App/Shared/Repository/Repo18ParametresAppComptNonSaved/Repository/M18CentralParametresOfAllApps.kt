package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Repo18CentralParametresOfAllApps(appDataBase: AppDatabase) {
    val repoTAG = "Repo18CentralParametresOfAllApps"

    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val refRepo = M18CentralParametresOfAllApps.ref
    private val dao = appDataBase.M18CentralParametresOfAllAppsDao()

    private val _data = mutableStateOf<M18CentralParametresOfAllApps?>(null)
    val dataValue by derivedStateOf { _data.value }

    init {
        repoScope.launch {
            // First, try to load from local database
            val localData = dao.getAll()
            if (localData != null) {
                _data.value = localData
                Log.d(repoTAG, "Loaded data from local database: $localData")
            }

            // Then setup Firebase sync
            setupFirebaseGet()
        }
    }

    private fun setupFirebaseGet() {
        refRepo.get().addOnSuccessListener { snapshot ->
            val data = snapshot.getValue(M18CentralParametresOfAllApps::class.java)

            repoScope.launch {
                if (data != null) {
                    dao.upsert(data)
                    _data.value = data
                    Log.d(repoTAG, "Successfully retrieved and saved data: $data")
                } else {
                    Log.w(repoTAG, "Received null data from Firebase")
                    // If Firebase returns null, use default values
                    val defaultData = M18CentralParametresOfAllApps.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Using default data: $defaultData")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(repoTAG, "Failed to retrieve data from Firebase", exception)

            // On Firebase failure, try to use local data or defaults
            repoScope.launch {
                val localData = dao.getAll()
                if (localData == null) {
                    val defaultData = M18CentralParametresOfAllApps.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Firebase failed, using default data: $defaultData")
                }
            }
        }
    }

    // Additional helper methods
    suspend fun getCurrentData(): M18CentralParametresOfAllApps? {
        return dao.getAll()
    }

    suspend fun refreshFromDatabase() {
        val dbData = dao.getAll()
        _data.value = dbData
        Log.d(repoTAG, "Refreshed data from database: $dbData")
    }

    suspend fun forceRefreshFromFirebase() {
        setupFirebaseGet()
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

    // Updated to match the actual Firebase data
    val au_Lence_Set_Compt_Ac_KeyId: String = "",

    val activeWindowsSearchProduit: Boolean = false,
    val devStartUpScree: String = Screen.FacadePresentoireProduits.route,
    var enablePerformAutoClickImageDisplayer: Boolean = false,
    val isControleFabVisible: Boolean = false,
) {
    companion object {
        val ref = RepositorysMainGetter.centralRef
            .child("Datas18CentralParametresOfAllApps")

        fun get_Default(): M18CentralParametresOfAllApps {
            return M18CentralParametresOfAllApps()
        }
    }
}

@Dao
interface M18CentralParametresOfAllAppsDao {
    @Query("SELECT COUNT(*) FROM M18CentralParametresOfAllApps")
    suspend fun getCount(): Int

    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M18CentralParametresOfAllApps WHERE keyId = 'M18CentralParametresOfAllApps'")
    suspend fun getAll(): M18CentralParametresOfAllApps?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M18CentralParametresOfAllApps)

    @Delete
    suspend fun delete(data: M18CentralParametresOfAllApps)

    @Query("DELETE FROM M18CentralParametresOfAllApps")
    suspend fun deleteAll()
}
