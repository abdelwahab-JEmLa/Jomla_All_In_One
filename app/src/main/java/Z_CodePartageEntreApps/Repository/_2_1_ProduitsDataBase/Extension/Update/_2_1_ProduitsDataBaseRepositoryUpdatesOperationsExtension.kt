package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.Update

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class _2_1_ProduitsDataBaseRepositoryUpdatesOperationsExtension(
    private val repositoryImpl: _2_1_ProduitsDataBase_RepositoryImpl
) {
    private val TAG = _2_1_ProduitsDataBase_Repository.TAG

    fun updateUnSeulData(
        data: _2_1_ProduitsDataBase,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>
    ) {
        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatasSnapList.indexOfFirst { it.vid == data.vid }
            if (recordIndex != -1) {
                modelDatasSnapList[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                appDatabase._2_1_ProduitsDataBaseDao().insert(data)
                firebaseUpdateData(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating data: ${e.message}")
            }
        }
    }

    fun deleteUnSeulData(
        data: _2_1_ProduitsDataBase,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>
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
                    _2_1_ProduitsDataBase_Repository.sonDataBaseRef.child(data.vid.toString()).removeValue().await()
                    appDatabase._2_1_ProduitsDataBaseDao().delete(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in deleteUnSeulData: ${e.message}")
        }
    }

    fun addData(
        data: _2_1_ProduitsDataBase,
        repositoryScope: CoroutineScope,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>
    ) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatasSnapList.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    _2_1_ProduitsDataBase_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
                    appDatabase._2_1_ProduitsDataBaseDao().insert(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addData: ${e.message}")
        }
    }

    private suspend fun firebaseUpdateData(data: _2_1_ProduitsDataBase) {
        try {
            _2_1_ProduitsDataBase_Repository.sonDataBaseRef.child(data.vid.toString()).setValue(data).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating Firebase data: ${e.message}")
        }
    }

    suspend fun updateMultiDatas(
        datas: SnapshotStateList<_2_1_ProduitsDataBase>,
        isUpdating: AtomicBoolean,
        appDatabase: AppDatabase,
        modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>,
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
                    appDatabase._2_1_ProduitsDataBaseDao().deleteAll()
                    appDatabase._2_1_ProduitsDataBaseDao().insertAll(datasList)
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
                            _2_1_ProduitsDataBase_Repository.sonDataBaseRef.removeEventListener(
                                it
                            )
                        }
                        isListenerActive.set(false)
                    }

                    synchronized(flowListenerLock) {
                        flowValueEventListener?.let {
                            _2_1_ProduitsDataBase_Repository.sonDataBaseRef.removeEventListener(
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
                            _2_1_ProduitsDataBase_Repository.sonDataBaseRef.addValueEventListener(
                                tempListener
                            )
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            _2_1_ProduitsDataBase_Repository.sonDataBaseRef.addValueEventListener(
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

    private fun batchFireBaseSet(datas: List<_2_1_ProduitsDataBase>) {
        try {
            val reference = _2_1_ProduitsDataBase_Repository.sonDataBaseRef
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
