package Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationRepositoryLogOperationsExtention
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class _1_2_ProduitAcheteOperationRepositoryImpl(
    private val appDatabase: AppDatabase
) : _1_2_ProduitAcheteOperation_Repository {
    private val TAG = _1_2_ProduitAcheteOperation_Repository.TAG

    override var modelDatasSnapList: SnapshotStateList<_1_2_ProduitAcheteOperation> =
        mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val isUpdating = AtomicBoolean(false)
    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    override val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()

    private val updatesOperations =
        _1_2_ProduitAcheteOperationRepositoryUpdatesOperaionsExtention()
    private val logOperations = _1_2_ProduitAcheteOperationRepositoryLogOperationsExtention(this)

    init {
        repositoryScope.launch {
            initialize_1_2_ProduitAcheteOperationRepository()
        }
    }
    // Add this method to the _1_2_ProduitAcheteOperationRepositoryImpl class

    override fun notifyDataChanged() {
        repositoryScope.launch {
            try {
                // Refresh data from Room
                val roomData = withContext(Dispatchers.IO) {
                    appDatabase._1_2_ProduitAcheteOperationDao().getAll()
                }

                // Update the snapshot list on the main thread
                withContext(Dispatchers.Main) {
                    modelDatasSnapList.clear()
                    modelDatasSnapList.addAll(roomData)
                }

                // Optionally log the refresh
                if (TAG.isNotEmpty()) {
                    Log.d(TAG, "Data refreshed: ${roomData.size} items")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in notifyDataChanged: ${e.message}")
            }
        }
    }

    override suspend fun ensureDataIsInitialized() {
        try {
            if (!initialDataLoaded) {
                withContext(Dispatchers.IO) {
                    // Wait until data is loaded
                    while (!initialDataLoaded) {
                        delay(100)
                        if (progressRepo.value >= 1.0f) {
                            initialDataLoaded = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ensuring data initialization: ${e.message}")
        }
    }

    // Implementing the required interface method
    override suspend fun add(produitAcheteOperation: _1_2_ProduitAcheteOperation): Long {
        return withContext(Dispatchers.IO) {
            try {
                val insertedId = appDatabase._1_2_ProduitAcheteOperationDao().add(produitAcheteOperation)
                val updatedData = produitAcheteOperation.copy(vid = insertedId)

                withContext(Dispatchers.Main) {
                    modelDatasSnapList.add(updatedData)
                }

                try {
                    _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef
                        .child(insertedId.toString())
                        .setValue(updatedData)
                        .await()
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Firebase after add: ${e.message}")
                }

                insertedId
            } catch (e: Exception) {
                Log.e(TAG, "Error in add method: ${e.message}")
                -1L
            }
        }
    }

    override fun updateUnSeulData(data: _1_2_ProduitAcheteOperation) {
        updatesOperations.updateUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    private suspend fun initialize_1_2_ProduitAcheteOperationRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()
            if (TAG.isNotEmpty()) {
                log()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repository: ${e.message}")
        }
    }
    override fun addDataAndReturneItVID(
        data: _1_2_ProduitAcheteOperation,
        onAddSuccess: (Long) -> Unit
    ) {
        try {
            // Create a copy of the data to work with
            val dataToAdd = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Insert into Room and get the new vid
                    val newVid = appDatabase._1_2_ProduitAcheteOperationDao().insertAvecRetureNewVid(dataToAdd)

                    // Update the object with the new vid
                    dataToAdd.vid = newVid

                    withContext(Dispatchers.Main) {
                        modelDatasSnapList.add(dataToAdd)
                    }

                    // Update Firebase with the new vid
                    _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.child(newVid.toString()).setValue(dataToAdd).await()

                    // Call the success callback with the new vid
                    onAddSuccess(newVid)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addDataAndReturnItVID: ${e.message}")
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase._1_2_ProduitAcheteOperationDao().getAll()
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading from Room: ${e.message}")
                    emptyList()
                }

                withContext(Dispatchers.Main) {
                    modelDatasSnapList.clear()
                    if (dataList.isNotEmpty()) {
                        modelDatasSnapList.addAll(dataList)
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Error in loadDepuitRoom: ${e.message}")
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            val roomCount = withContext(Dispatchers.IO) {
                try {
                    appDatabase._1_2_ProduitAcheteOperationDao().getCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Room count: ${e.message}")
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.get()
                    Tasks.await(task)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting Firebase snapshot: ${e.message}")
                null
            }

            val firebaseCount = firebaseSnapshot?.childrenCount?.toInt() ?: 0

            if (roomCount != firebaseCount || roomCount == 0) {
                if (firebaseCount > 0) {
                    importDeFireBaseAuRoom(repositoryScope)
                }
            }

            withContext(Dispatchers.Main) {
                FireBaseOnDataChangeListner()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                FireBaseOnDataChangeListner()
            }
            Log.e(TAG, "Error in checkDataConsistency: ${e.message}")
        }
    }

    private fun FireBaseOnDataChangeListner() {
        synchronized(flowListenerLock) {
            removeFlowDataListener()

            if (!isFlowListenerActive.get()) {
                flowValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val updatedList = mutableListOf<_1_2_ProduitAcheteOperation>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(_1_2_ProduitAcheteOperation::class.java)
                                data?.let {
                                    updatedList.add(it)
                                }
                            }

                            repositoryScope.launch(Dispatchers.Main) {
                                if (updatedList.isNotEmpty()) {
                                    modelDatasSnapList.clear()
                                    modelDatasSnapList.addAll(updatedList)
                                }
                            }

                            repositoryScope.launch(Dispatchers.IO) {
                                try {
                                    appDatabase._1_2_ProduitAcheteOperationDao().deleteAll()
                                    appDatabase._1_2_ProduitAcheteOperationDao().insertAll(updatedList)
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Error updating Room from Firebase listener: ${e.message}"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in Firebase data listener: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Firebase listener cancelled: ${error.message}")
                    }
                }

                _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing flow listener: ${e.message}")
                } finally {
                    flowValueEventListener = null
                    isFlowListenerActive.set(false)
                }
            }
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatasSnapList.clear()
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val task = _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase._1_2_ProduitAcheteOperationDao().deleteAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting Room data: ${e.message}")
                    }

                    val dataList = mutableListOf<_1_2_ProduitAcheteOperation>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(_1_2_ProduitAcheteOperation::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firebase data: ${e.message}")
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase._1_2_ProduitAcheteOperationDao().insertAll(dataList)

                            withContext(Dispatchers.Main) {
                                modelDatasSnapList.addAll(dataList)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error inserting data to Room: ${e.message}")
                        }
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    progressRepo.value = 0f
                    Log.e(TAG, "Error importing from Firebase: ${e.message}")
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Error in importDeFireBaseAuRoom: ${e.message}")
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            if (isListenerActive.get() && valueEventListener != null) {
                try {
                    _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data listener: ${e.message}")
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun deleteUnSeulData(data: _1_2_ProduitAcheteOperation) {
        updatesOperations.deleteUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    fun addData(data: _1_2_ProduitAcheteOperation) {
        updatesOperations.addData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<_1_2_ProduitAcheteOperation>) {
        updatesOperations.updateMultiDatas(
            datas,
            isUpdating,
            appDatabase,
            modelDatasSnapList,
            valueEventListener,
            flowValueEventListener,
            listenerLock,
            flowListenerLock,
            isListenerActive,
            isFlowListenerActive
        )
    }

    fun cleanup() {
        repositoryScope.launch {
            removeDataChangeListener()
            removeFlowDataListener()
        }
    }

    fun onDestroy() {
        cleanup()
    }

    fun log() {
        logOperations.log(
            modelDatasSnapList.size,
            initialDataLoaded,
            progressRepo.value,
            lastUpdateTimestamp,
            isListenerActive.get(),
            isFlowListenerActive.get()
        )
    }
}
