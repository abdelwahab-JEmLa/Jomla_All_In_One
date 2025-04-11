package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.Log._2_1_ProduitsDataBaseRepositoryLogOperationsExtension
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.Update._2_1_ProduitsDataBaseRepositoryUpdatesOperationsExtension
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

class _2_1_ProduitsDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase,
) : _2_1_ProduitsDataBase_Repository {
    private val TAG = _2_1_ProduitsDataBase_Repository.TAG

    override var modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase> =
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

    private val updatesOperations = _2_1_ProduitsDataBaseRepositoryUpdatesOperationsExtension(this)
    private val logOperations = _2_1_ProduitsDataBaseRepositoryLogOperationsExtension(this)

    init {
        repositoryScope.launch {
            initialize_2_1_ProduitsDataBaseRepository()
        }
    }

    override fun addDataAndReturneItVID(
        data: _2_1_ProduitsDataBase,
        onAddSuccess: (Long) -> Unit
    ) {
        try {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Insert into Room and get the new vid
                    val newVid = appDatabase._2_1_ProduitsDataBaseDao().insertAvecRetureNewVid(data)

                    // Update the object with the new vid
                    data.vid = newVid
                    withContext(Dispatchers.Main) {
                        modelDatasSnapList.add(data)
                    }

                    // Update Firebase with the new vid
                    _2_1_ProduitsDataBase_Repository.sonDataBaseRef.child(newVid.toString()).setValue(data).await()

                    // Call the success callback with the new vid
                    onAddSuccess(newVid)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addDataAndReturneItVID: ${e.message}")
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

    override fun getIdParNomModel(nomModel: String): Long {
        val vendor = modelDatasSnapList.find { it.deviceModelNom == nomModel }
        if (vendor == null) {
            Log.e(TAG, "Vendor not found for model: $nomModel. Available models: ${modelDatasSnapList.map { it.deviceModelNom }}")
            return -1L
        }
        return vendor.vid
    }

    override fun updateUnSeulData(data: _2_1_ProduitsDataBase) {
        updatesOperations.updateUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }


    private suspend fun initialize_2_1_ProduitsDataBaseRepository() {
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
                    appDatabase._2_1_ProduitsDataBaseDao().getAll()
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
                    appDatabase._2_1_ProduitsDataBaseDao().getCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Room count: ${e.message}")
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = _2_1_ProduitsDataBase_Repository.sonDataBaseRef.get()
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
                            val updatedList = mutableListOf<_2_1_ProduitsDataBase>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(_2_1_ProduitsDataBase::class.java)
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
                                    appDatabase._2_1_ProduitsDataBaseDao().deleteAll()
                                    appDatabase._2_1_ProduitsDataBaseDao().insertAll(updatedList)
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

                _2_1_ProduitsDataBase_Repository.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    _2_1_ProduitsDataBase_Repository.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
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
                    val task = _2_1_ProduitsDataBase_Repository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase._2_1_ProduitsDataBaseDao().deleteAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting Room data: ${e.message}")
                    }

                    val dataList = mutableListOf<_2_1_ProduitsDataBase>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(_2_1_ProduitsDataBase::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firebase data: ${e.message}")
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase._2_1_ProduitsDataBaseDao().insertAll(dataList)

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
                    _2_1_ProduitsDataBase_Repository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data listener: ${e.message}")
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun deleteUnSeulData(data: _2_1_ProduitsDataBase) {
        updatesOperations.deleteUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override fun addData(data: _2_1_ProduitsDataBase) {
        updatesOperations.addData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<_2_1_ProduitsDataBase>) {
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
