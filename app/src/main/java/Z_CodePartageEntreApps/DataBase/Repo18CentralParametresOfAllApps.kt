package Z_CodePartageEntreApps.DataBase

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Repo18CentralParametresOfAllApps(appDataBase: AppDatabase) {
    val repoTAG = "Repo18CentralParametresOfAllApps"

    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val refRepo = M00CentralParametresOfAllApps.Companion.ref
    private val dao = appDataBase.M18CentralParametresOfAllAppsDao()

    private val _data = mutableStateOf<M00CentralParametresOfAllApps?>(null)
    val dataValue by derivedStateOf { _data.value }

    init {
        extracted()
    }

    private fun extracted() {
        repoScope.launch {
            val localData = dao.getAll()
            _data.value = localData
            setupFirebaseGet()
        }
    }

    private fun setupFirebaseGet() {
        refRepo.get().addOnSuccessListener { snapshot ->
            val data = snapshot.getValue(M00CentralParametresOfAllApps::class.java)

            repoScope.launch {
                if (data != null) {
                    dao.upsert(data)
                    _data.value = data
                    Log.d(repoTAG, "Successfully retrieved and saved data: $data")
                } else {
                    Log.w(repoTAG, "Received null data from Firebase")
                    val defaultData = M00CentralParametresOfAllApps.Companion.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Using default data: $defaultData")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(repoTAG, "Failed to retrieve data from Firebase", exception)

            repoScope.launch {
                val localData = dao.getAll()
                if (localData == null) {
                    val defaultData = M00CentralParametresOfAllApps.Companion.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Firebase failed, using default data: $defaultData")
                }
            }
        }
    }

    fun getNotificationSettings(): Boolean {
        return _data.value?.enableNotifications ?: true
    }
}

@Dao
interface M18CentralParametresOfAllAppsDao {
    @Query("SELECT COUNT(*) FROM M00CentralParametresOfAllApps")
    suspend fun getCount(): Int

    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M00CentralParametresOfAllApps WHERE keyId = 'M18CentralParametresOfAllApps'")
    suspend fun getAll(): M00CentralParametresOfAllApps?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M00CentralParametresOfAllApps)

    @Delete
    suspend fun delete(data: M00CentralParametresOfAllApps)

    @Query("DELETE FROM M00CentralParametresOfAllApps")
    suspend fun deleteAll()
}

