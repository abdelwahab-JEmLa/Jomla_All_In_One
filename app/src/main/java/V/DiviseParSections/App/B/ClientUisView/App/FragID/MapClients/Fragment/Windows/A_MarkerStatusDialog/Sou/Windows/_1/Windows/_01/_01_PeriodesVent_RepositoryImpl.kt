package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class _01_PeriodesVent_RepositoryImpl(
     val appDatabase: AppDatabase,
) : _01_PeriodesVent_Repository {
    private val TAG = "_01_PeriodesVentNoSQl"

    override var modelDatasSnapList: SnapshotStateList<_01_PeriodesVentNoSQl> =
        mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

     var _01_PeriodesVentRoomSQl: SnapshotStateList<_01_PeriodesVentRoomSQl> =
        mutableStateListOf()
     var _02_VendeursActiveDonsCettePeriodeRoomSQlModel:
             SnapshotStateList<_02_VendeursActiveDonsCettePeriodeRoomSQlModel> =
        mutableStateListOf()
     var _03_ProduitsVenduParLuiRoomSQlModel:
             SnapshotStateList<_03_ProduitsVenduParLuiRoomSQlModel> =
        mutableStateListOf()

    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()


    init {
        repositoryScope.launch {
            initialize_01_PeriodesVentRepository()
        }
    }

    private suspend fun initialize_01_PeriodesVentRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repository: ${e.message}")
        }
    }
     suspend fun ensureDataIsInitialized() {
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
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase._01_PeriodesVentRoomSQlModelDao().getAll()
                } catch (e: Exception) {
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
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            val roomCount = withContext(Dispatchers.IO) {
                try {
                    appDatabase._01_PeriodesVentRoomSQlModelDao().getCount()
                } catch (e: Exception) {
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = _01_PeriodesVent_Repository.sonDataBaseRef.get()
                    Tasks.await(task)
                }
            } catch (e: Exception) {
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
        }
    }

    private fun FireBaseOnDataChangeListner() {
        synchronized(flowListenerLock) {
            removeFlowDataListener()

            if (!isFlowListenerActive.get()) {
                flowValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val updatedList = mutableListOf<_01_PeriodesVentNoSQl>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(_01_PeriodesVentNoSQl::class.java)
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
                                    appDatabase._01_PeriodesVentRoomSQlModelDao().deleteAll()
                                    appDatabase._01_PeriodesVentRoomSQlModelDao().insertAll(updatedList)
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                    )
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }

                _01_PeriodesVent_Repository.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    _01_PeriodesVent_Repository.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
                } catch (e: Exception) {
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
                    val task = _01_PeriodesVent_Repository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase._01_PeriodesVentRoomSQlModelDao().deleteAll()
                    } catch (e: Exception) {
                    }

                    val dataList = mutableListOf<_01_PeriodesVentNoSQl>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(_01_PeriodesVentNoSQl::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase._01_PeriodesVentRoomSQlModelDao().insertAll(dataList)

                            withContext(Dispatchers.Main) {
                                modelDatasSnapList.addAll(dataList)
                            }
                        } catch (e: Exception) {
                        }
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    progressRepo.value = 0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            if (isListenerActive.get() && valueEventListener != null) {
                try {
                    _01_PeriodesVent_Repository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
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


}
