package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import EntreApps.Shared.Modules.Base.AppDatabase
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

class _DataBaseFactory_MVentPeriodeImpl(
    private val appDatabase: AppDatabase
) : DataBaseFactoryMVentPeriode {
    val dao = appDatabase.MVentPeriodeDao()

    private val TAG = DataBaseFactoryMVentPeriode.TAG

    override var modelDatasSnapList: SnapshotStateList<MVentPeriode> =
        mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)
    override val activeId = MutableStateFlow(0L)

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

    private val updatesOperations = _1_4_PeriodeVentRepositoryUpdatesOperaionsExtention(this)
    private val logOperations = _1_4_PeriodeVentRepositoryLogOperationsExtention(this)

    override fun addOrUpdatedDataBase(
        existingIndex: Int,
        dataAvecTigerUpdate: MVentPeriode
    ) {
        repositoryScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateMVentPeriode(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateMVentPeriode(listOf(dataAvecTigerUpdate))
            }
        }
    }


    private suspend fun batchFireBaseUpdateMVentPeriode(datas: List<MVentPeriode>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        MVentPeriode.ref.updateChildren(updates).await()
    }



    init {
        repositoryScope.launch {
            initialize_1_4_PeriodeVentRepository()
        }
    }
    override fun updateUnSeulData(data: MVentPeriode) {
    }


    override fun addData(data: MVentPeriode) {
        addOrUpdatedDataBase(-1,data)
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


    override fun getByMainVAl(): Long {
        return modelDatasSnapList.last().vid
    }


    private suspend fun initialize_1_4_PeriodeVentRepository() {
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
                    appDatabase.MVentPeriodeDao().getAll()
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
                    appDatabase.MVentPeriodeDao().getCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Room count: ${e.message}")
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = DataBaseFactoryMVentPeriode.sonDataBaseRef.get()
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
                            val updatedList = mutableListOf<MVentPeriode>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(MVentPeriode::class.java)
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
                                    appDatabase.MVentPeriodeDao().deleteAll()
                                    appDatabase.MVentPeriodeDao().insertAll(updatedList)
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

                M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                DataBaseFactoryMVentPeriode.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)}
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    DataBaseFactoryMVentPeriode.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
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
                    val task = DataBaseFactoryMVentPeriode.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase.MVentPeriodeDao().deleteAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting Room data: ${e.message}")
                    }

                    val dataList = mutableListOf<MVentPeriode>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(MVentPeriode::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firebase data: ${e.message}")
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase.MVentPeriodeDao().insertAll(dataList)

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
                    DataBaseFactoryMVentPeriode.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data listener: ${e.message}")
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun deleteUnSeulData(data: MVentPeriode) {
        updatesOperations.deleteUnSeulData(data, repositoryScope, appDatabase, modelDatasSnapList)
    }

    override fun addDataAndReturneItVID(
        data: MVentPeriode,
        onAddSuccess: (Long) -> Unit
    ) {
    }


    override suspend fun updateMultiDatas(datas: SnapshotStateList<MVentPeriode>) {
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
