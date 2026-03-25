package Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand.Extension.Update

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_RepositoryImpl
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class _4_CouleurOperationCommandRepositoryUpdatesOperationsExtension(
    private val repositoryImpl: _4_CouleurOperationCommand_RepositoryImpl
) {
    private val TAG = _4_CouleurOperationCommand_Repository.TAG

    fun updateUnSeulData(
        data: _4_CouleurOperationCommand,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand>
    ) {
        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatasSnapList.indexOfFirst { it.vid == data.vid }
            if (recordIndex != -1) {
                modelDatasSnapList[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                appDatabase._4_CouleurOperationCommandDao().insert(data)
                firebaseUpdateData(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating data: ${e.message}")
            }
        }
    }

    fun deleteUnSeulData(
        data: _4_CouleurOperationCommand,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand>
    ) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatasSnapList.indexOfFirst { it.vid == data.vid }
                if (recordIndex != -1) {
                    modelDatasSnapList.removeAt(recordIndex)
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(data.vid.toString()).removeValue().await()
                    appDatabase._4_CouleurOperationCommandDao().delete(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertUneDataEtReturnVID: ${e.message}")
        }
    }

    fun addData(
        data: _4_CouleurOperationCommand,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand>
    ) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatasSnapList.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
                    appDatabase._4_CouleurOperationCommandDao().insert(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addData: ${e.message}")
        }
    }

    private suspend fun firebaseUpdateData(data: _4_CouleurOperationCommand) {
        try {
            _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating Firebase data: ${e.message}")
        }
    }

    suspend fun updateMultiDatas(
        datas: SnapshotStateList<_4_CouleurOperationCommand>,
        isUpdating: AtomicBoolean,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand>,
        valueEventListener: ValueEventListener?,
        flowValueEventListener: ValueEventListener?,
        listenerLock: Any,
        flowListenerLock: Any,
        isListenerActive: AtomicBoolean,
        isFlowListenerActive: AtomicBoolean
    ) {
        if (isUpdating.getAndSet(true)) {
            return
        }

        try {
            val datasList = datas.toList()

            withContext(Dispatchers.IO) {
                try {
                    appDatabase._4_CouleurOperationCommandDao().deleteAll()
                    appDatabase._4_CouleurOperationCommandDao().insertAll(datasList)
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Room database: ${e.message}")
                }
            }

            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener
                val tempFlowListener = flowValueEventListener

                try {
                    synchronized(listenerLock) {
                        valueEventListener?.let {
                            _4_CouleurOperationCommand_Repository.sonDataBaseRef.removeEventListener(
                                it
                            )
                        }
                        isListenerActive.set(false)
                    }

                    synchronized(flowListenerLock) {
                        flowValueEventListener?.let {
                            _4_CouleurOperationCommand_Repository.sonDataBaseRef.removeEventListener(
                                it
                            )
                        }
                        isFlowListenerActive.set(false)
                    }

                    batchFireBaseSet(datasList)

                } catch (e: Exception) {
                    Log.e(TAG, "Error in synchronized block: ${e.message}")
                } finally {
                    synchronized(listenerLock) {
                        if (!isListenerActive.get() && tempListener != null) {
                            M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                            _4_CouleurOperationCommand_Repository.sonDataBaseRef.addValueEventListener(
                                tempListener
                            )}
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                            _4_CouleurOperationCommand_Repository.sonDataBaseRef.addValueEventListener(
                                tempFlowListener
                            )}
                            isFlowListenerActive.set(true)
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                modelDatasSnapList.clear()
                modelDatasSnapList.addAll(datas)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateMultiDatas: ${e.message}")
        } finally {
            isUpdating.set(false)
        }
    }

    private fun batchFireBaseSet(datas: List<_4_CouleurOperationCommand>) {
        try {
            val reference = _4_CouleurOperationCommand_Repository.sonDataBaseRef
            val batchUpdates = HashMap<String, Any>()

            for (data in datas) {
                batchUpdates[data.vid.toString()] = data
            }

            reference.updateChildren(batchUpdates)
                .addOnSuccessListener {
                    Log.d(TAG, "Batch upsertLenceCommandeRepoGroupedProtoAvantJuin3 successful for ${datas.size} items")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Batch upsertLenceCommandeRepoGroupedProtoAvantJuin3 failed: ${exception.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchFireBaseSet: ${e.message}")
        }
    }
}
