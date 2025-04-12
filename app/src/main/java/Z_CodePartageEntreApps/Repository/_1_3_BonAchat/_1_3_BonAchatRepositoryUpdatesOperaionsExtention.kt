package Z_CodePartageEntreApps.Repository._1_3_BonAchat

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models._1_3_BonAchat
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class _1_3_BonAchatRepositoryUpdatesOperaionsExtention(
    private val repositoryImpl: _1_3_BonAchatRepositoryImpl
) {
    private val TAG = _1_3_BonAchat_Repository.TAG
    private val logOperations = _1_3_BonAchatRepositoryLogOperationsExtention(repositoryImpl)

    fun updateUnSeulData(
        data: _1_3_BonAchat,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_1_3_BonAchat>
    ) {
        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatasSnapList.indexOfFirst { it.vid == data.vid }
            if (recordIndex != -1) {
                modelDatasSnapList[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                appDatabase._1_3_BonAchatDao().insert(data)
                firebaseUpdateData(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating data: ${e.message}")
            }
        }
    }

    fun deleteUnSeulData(
        data: _1_3_BonAchat,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_1_3_BonAchat>
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
                    _1_3_BonAchat_Repository.sonDataBaseRef.child(data.vid.toString()).removeValue().await()
                    appDatabase._1_3_BonAchatDao().delete(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in deleteUnSeulData: ${e.message}")
        }
    }

    fun addData(
        data: _1_3_BonAchat,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_1_3_BonAchat>
    ) {
        try {
            // Log the data addition
            logOperations.logDataAdd(data)

            repositoryScope.launch(Dispatchers.Main) {
                modelDatasSnapList.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    _1_3_BonAchat_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
                    appDatabase._1_3_BonAchatDao().insert(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addData: ${e.message}")
        }
    }

    private suspend fun firebaseUpdateData(data: _1_3_BonAchat) {
        try {
            _1_3_BonAchat_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating Firebase data: ${e.message}")
        }
    }

    suspend fun updateMultiDatas(
        datas: SnapshotStateList<_1_3_BonAchat>,
        isUpdating: AtomicBoolean,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_1_3_BonAchat>,
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
                    appDatabase._1_3_BonAchatDao().deleteAll()
                    appDatabase._1_3_BonAchatDao().insertAll(datasList)
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
                            _1_3_BonAchat_Repository.sonDataBaseRef.removeEventListener(
                                it
                            )
                        }
                        isListenerActive.set(false)
                    }

                    synchronized(flowListenerLock) {
                        flowValueEventListener?.let {
                            _1_3_BonAchat_Repository.sonDataBaseRef.removeEventListener(
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
                            _1_3_BonAchat_Repository.sonDataBaseRef.addValueEventListener(
                                tempListener
                            )
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            _1_3_BonAchat_Repository.sonDataBaseRef.addValueEventListener(
                                tempFlowListener
                            )
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

    private fun batchFireBaseSet(datas: List<_1_3_BonAchat>) {
        try {
            val reference = _1_3_BonAchat_Repository.sonDataBaseRef
            val batchUpdates = HashMap<String, Any>()

            for (data in datas) {
                batchUpdates[data.vid.toString()] = data
            }

            reference.updateChildren(batchUpdates)
                .addOnSuccessListener {
                    Log.d(TAG, "Batch update successful for ${datas.size} items")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Batch update failed: ${exception.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchFireBaseSet: ${e.message}")
        }
    }
}
