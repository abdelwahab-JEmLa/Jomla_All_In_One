package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init.initializeDataReturn
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class D_EtateMessageVocaleRepository(
    val context: Context,
    appDatabase: AppDatabase,

) {
    val repoTAG = "D_EtateMessageVocaleRepository"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    data class RepoState(
        val modelListFlow: List<D_EtateMessageVocale>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.D_EtateMessageVocaleDao()
    val repoRef = D_EtateMessageVocale.caRef

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)
        }
    }

    suspend fun updateRepoState(data: List<D_EtateMessageVocale>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }
        triggerUpdateFbParTimestampsListener()
    }
  

    fun triggerUpdateFbParTimestampsListener() {
        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        processD_EtateMessageVocaleSnapshot(snapshot)
                    } catch (e: Exception) {
                        Log.e("FirebaseListener", "Error in onDataChange for D_EtateMessageVocale", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseListener", "Firebase listener cancelled: ${error.message}", error.toException())
            }
        })
    }

    private suspend fun processD_EtateMessageVocaleSnapshot(snapshot: DataSnapshot) {
        var updateCount = 0
        val entityName = "D_EtateMessageVocale"

        for (child in snapshot.children) {
            try {
                child.getValue(D_EtateMessageVocale::class.java)?.let { entity ->
                    val entityWithKey = entity.copy(keyFireBase = child.key ?: "")
                    if (shouldUpdateD_EtateMessageVocaleWithComparison(entityWithKey,0)) {
                        dao.upsert(entityWithKey)
                        updateCount++
                    }
                }
            } catch (e: Exception) {
                Log.w("FirebaseListener", "Failed to process child ${child.key}", e)
            }
        }

        if (updateCount > 0) {
            val allData = dao.getAll()
            updateRepoState(allData)
            Log.d("FirebaseListener", "Updated $updateCount $entityName records")
        }
    }

    private fun shouldUpdateD_EtateMessageVocaleWithComparison(
        entity: D_EtateMessageVocale,
        lastListeningTimestamp: Long
    ): Boolean {
        return try {
            entity.dernierFireBaseUpdateTimestamps > lastListeningTimestamp
        } catch (e: Exception) {
            Log.w("FirebaseListener", "Error checking timestamp for D_EtateMessageVocale", e)
            true
        }
    }
}
