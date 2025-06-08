package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init.initializeDataReturn
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import android.content.Context
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
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
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
    private var isListenerRegistered = false

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

        if (!isListenerRegistered) {
            triggerUpdateFbParTimestampsListener()
        }
    }

    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(D_EtateMessageVocale::class.java)?.let { entity ->
                                    val entityWithKey = entity.copy(keyFireBase = child.key ?: "")
                                    val shouldUpdate = try {
                                        val localEntity = dao.getAll().find { it.keyFireBase == entityWithKey.keyFireBase }
                                        if (localEntity == null) {
                                            true
                                        } else {
                                            entityWithKey.dernierFireBaseUpdateTimestamps > localEntity.dernierFireBaseUpdateTimestamps
                                        }
                                    } catch (e: Exception) {
                                        true
                                    }

                                    if (shouldUpdate) {
                                        dao.upsert(entityWithKey)
                                        updateCount++
                                    }
                                }
                            } catch (e: Exception) {}
                        }

                        if (updateCount > 0) {
                            val allData = dao.getAll()
                            withContext(Dispatchers.Main) {
                                val newRepoState = RepoState(
                                    modelListFlow = allData,
                                    mainProgressRepo = 1.0f
                                )
                                _repoState.value = newRepoState
                            }
                        }
                    } catch (e: Exception) {}
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
            }
        })
    }

    fun testTriggerUpdateFbParTimestampsListener() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingTestEntity = dao.getAll().find { it.keyFireBase.contains("TEST_") }

                if (existingTestEntity != null) {
                    val updatedTestEntity = existingTestEntity.copy(
                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(updatedTestEntity.keyFireBase).setValue(updatedTestEntity)
                } else {
                    val testEntity = D_EtateMessageVocale.createTestInstance().first()
                    val testEntityWithTimestamp = testEntity.copy(
                        keyFireBase = "TEST_${System.currentTimeMillis()}"
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(testEntityWithTimestamp.keyFireBase).setValue(testEntityWithTimestamp)
                }
            } catch (e: Exception) {}
        }
    }
}
