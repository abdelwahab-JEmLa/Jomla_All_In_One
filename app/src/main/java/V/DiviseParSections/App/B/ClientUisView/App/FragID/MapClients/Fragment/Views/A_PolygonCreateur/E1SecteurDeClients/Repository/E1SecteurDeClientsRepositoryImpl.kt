package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
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

class E1SecteurDeClientsRepositoryImpl(
    val appDatabase: AppDatabase
) : E1SecteurDeClientsRepository {
    private val TAG = E1SecteurDeClientsRepository.TAG

    override var modelDatasSnapList: SnapshotStateList<E1SecteurDeClients> =
        mutableStateListOf()

    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()


    init {
        repositoryScope.launch {
            initializeE1SecteurDeClientsRepository()
        }
    }

    override fun getOuvertE1SecteurDeClients(): E1SecteurDeClients? {
        return modelDatasSnapList.find { it.ouvert }
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



    private suspend fun initializeE1SecteurDeClientsRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repository: ${e.message}")
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase.e1SecteurDeClientsDao().getAll()
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
                    appDatabase.e1SecteurDeClientsDao().getCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Room count: ${e.message}")
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = E1SecteurDeClientsRepository.sonDataBaseRef.get()
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
                            val updatedList = mutableListOf<E1SecteurDeClients>()
                            for (dataSnapshot in snapshot.children) {
                                val data = dataSnapshot.getValue(E1SecteurDeClients::class.java)
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
                                    appDatabase.e1SecteurDeClientsDao().deleteAll()
                                    appDatabase.e1SecteurDeClientsDao().insertAll(updatedList)
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

                E1SecteurDeClientsRepository.sonDataBaseRef.addValueEventListener(flowValueEventListener!!)
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    E1SecteurDeClientsRepository.sonDataBaseRef.removeEventListener(flowValueEventListener!!)
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
                    val task = E1SecteurDeClientsRepository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase.e1SecteurDeClientsDao().deleteAll()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting Room data: ${e.message}")
                    }

                    val dataList = mutableListOf<E1SecteurDeClients>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(E1SecteurDeClients::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firebase data: ${e.message}")
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase.e1SecteurDeClientsDao().insertAll(dataList)

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
                    E1SecteurDeClientsRepository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data listener: ${e.message}")
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
