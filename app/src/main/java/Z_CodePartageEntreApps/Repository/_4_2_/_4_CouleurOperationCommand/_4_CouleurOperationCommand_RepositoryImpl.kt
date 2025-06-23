package Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand.Extension.Log._4_CouleurOperationCommandRepositoryLogOperationsExtension
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand.Extension.Update._4_CouleurOperationCommandRepositoryUpdatesOperationsExtension
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

class _4_CouleurOperationCommand_RepositoryImpl(
     val appDatabase: AppDatabase,
) : _4_CouleurOperationCommand_Repository {
    private val TAG = _4_CouleurOperationCommand_Repository.TAG

    override var modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand> =
        mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val isUpdating = AtomicBoolean(false)
    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()

    private val updatesOperations = _4_CouleurOperationCommandRepositoryUpdatesOperationsExtension(this)
    private val logOperations = _4_CouleurOperationCommandRepositoryLogOperationsExtension(this)

    init {
        repositoryScope.launch {
            initialize_4_CouleurOperationCommandRepository()
        }
    }

    override fun addDataAndReturnItVID(
        data: _4_CouleurOperationCommand,
        onAddSuccess: (Long) -> Unit
    ) {
        try {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Insert into Room and get the new vid
                    val newVid = appDatabase._4_CouleurOperationCommandDao().insertAvecRetureNewVid(data)

                    // Update the object with the new vid
                    data.vid = newVid
                    withContext(Dispatchers.Main) {
                        modelDatasSnapList.add(data)
                    }

                    // Update Firebase with the new vid
                    _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(newVid.toString()).setValue(data).await()

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

    override fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_4_CouleurOperationCommand>,
        onAddSuccess: (List<Long>) -> Unit
    ) {
        try {
            if (dataList.isEmpty()) {
                Log.w(TAG, "addMultiDATAsEtReturnVIDsList: Empty data list provided")
                onAddSuccess(emptyList())
                return
            }

            Log.d(TAG, "addMultiDATAsEtReturnVIDsList: Starting to upsert ${dataList.size} items")

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Insert into Room and get the VIDs
                    val newVids = appDatabase._4_CouleurOperationCommandDao().insertAllAndReturnVids(dataList)
                    Log.d(TAG, "Room insertion successful. Got ${newVids.size} VIDs")

                    // Update the objects with their new vids
                    dataList.forEachIndexed { index, data ->
                        if (index < newVids.size) {
                            data.vid = newVids[index]
                        }
                    }

                    // Update the UI list
                    withContext(Dispatchers.Main) {
                        modelDatasSnapList.addAll(dataList)
                        Log.d(TAG, "Updated UI list with ${dataList.size} items")
                    }

                    // Update Firebase with the new items
                    try {
                        val updates = mutableMapOf<String, Any>()
                        dataList.forEach { data ->
                            updates[data.vid.toString()] = data
                        }

                        _4_CouleurOperationCommand_Repository.sonDataBaseRef.updateChildren(updates).await()
                        Log.d(TAG, "Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3 successful")
                    } catch (e: Exception) {
                        Log.e(TAG, "Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3 failed: ${e.message}", e)
                    }

                    // Call the success callback with the new vids
                    withContext(Dispatchers.Main) {
                        onAddSuccess(newVids)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding multiple data: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        onAddSuccess(emptyList())
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addMultiDATAsEtReturnVIDsList: ${e.message}", e)
            onAddSuccess(emptyList())
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

    override fun deleteAllEtRestartSequenceces() {
        repositoryScope.launch(Dispatchers.IO) {
            try {
                // Delete from Room and reset sequence
                appDatabase._4_CouleurOperationCommandDao().deleteAll()

                // Note: SQLite auto-increment sequences always start from 1, not 0
                // This is expected behavior and not an error
                appDatabase._4_CouleurOperationCommandDao().restartSequence()

                // Clear snapshot list
                withContext(Dispatchers.Main) {
                    modelDatasSnapList.clear()
                }

                // Delete from Firebase
                _4_CouleurOperationCommand_Repository.sonDataBaseRef.removeValue().await()

                Log.d(TAG, "Successfully deleted all data and restarted sequences")
            } catch (e: Exception) {
                Log.e(TAG, "Error in deleteAllEtRestartSequenceces: ${e.message}")
            }
        }
    }
    override fun upsertUneDataEtReturnVID(data: _4_CouleurOperationCommand, onSuccess: (Long) -> Unit): Unit {
        try {
            // Create add copy of the data to work with
            val dataToUpsert = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Check if the data already exists (if it has add valid vid)
                    if (dataToUpsert.vid > 0) {
                        // Update existing data
                        appDatabase._4_CouleurOperationCommandDao().insert(dataToUpsert)

                        // Update in snapshot list
                        withContext(Dispatchers.Main) {
                            val index = modelDatasSnapList.indexOfFirst { it.vid == dataToUpsert.vid }
                            if (index >= 0) {
                                modelDatasSnapList[index] = dataToUpsert
                            } else {
                                modelDatasSnapList.add(dataToUpsert)
                            }
                        }

                        // Update in Firebase
                        _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(dataToUpsert.vid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the existing vid
                        onSuccess(dataToUpsert.vid)
                    } else {
                        // If no valid vid, upsertEtReturnSonNewVid as new (same as addDataAndReturneItVID)
                        val newVid = appDatabase._4_CouleurOperationCommandDao().insertAvecRetureNewVid(dataToUpsert)

                        // Update the object with the new vid
                        dataToUpsert.vid = newVid

                        withContext(Dispatchers.Main) {
                            modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase with the new vid
                        _4_CouleurOperationCommand_Repository.sonDataBaseRef.child(newVid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the new vid
                        onSuccess(newVid)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error upserting data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertUnSeulDataEtReturnVID: ${e.message}")
        }
    }


    override fun updateUnSeulData(data: _4_CouleurOperationCommand) {
        updatesOperations.updateUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }


    private suspend fun initialize_4_CouleurOperationCommandRepository() {
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

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase._4_CouleurOperationCommandDao().getAll()
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
                    appDatabase._4_CouleurOperationCommandDao().getCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Room count: ${e.message}")
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = _4_CouleurOperationCommand_Repository.sonDataBaseRef.get()
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
                            val updatedList = mutableListOf<_4_CouleurOperationCommand>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(_4_CouleurOperationCommand::class.java)
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
                                    appDatabase._4_CouleurOperationCommandDao().deleteAll()
                                    appDatabase._4_CouleurOperationCommandDao().insertAll(updatedList)
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

                _4_CouleurOperationCommand_Repository.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    _4_CouleurOperationCommand_Repository.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
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
                    val task = _4_CouleurOperationCommand_Repository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase._4_CouleurOperationCommandDao().deleteAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting Room data: ${e.message}")
                    }

                    val dataList = mutableListOf<_4_CouleurOperationCommand>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(_4_CouleurOperationCommand::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firebase data: ${e.message}")
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase._4_CouleurOperationCommandDao().insertAll(dataList)

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
                    _4_CouleurOperationCommand_Repository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data listener: ${e.message}")
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun deleteUnSeulData(data: _4_CouleurOperationCommand) {
        updatesOperations.deleteUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override fun addData(data: _4_CouleurOperationCommand) {
        updatesOperations.addData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<_4_CouleurOperationCommand>) {
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
