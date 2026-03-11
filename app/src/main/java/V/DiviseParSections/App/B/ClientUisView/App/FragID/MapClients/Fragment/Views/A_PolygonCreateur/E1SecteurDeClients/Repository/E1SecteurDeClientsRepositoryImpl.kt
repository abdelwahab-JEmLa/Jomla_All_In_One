package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class E1SecteurDeClientsRepositoryImpl(
    val appDatabase: AppDatabase,
) : E1SecteurDeClientsRepository {
    override var listCollected: SnapshotStateList<E1SecteurDeClients> = SnapshotStateList()

    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val mainDao = appDatabase.e1SecteurDeClientsDao()

    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()

    init {
        repositoryScope.launch {
            initializeE1SecteurDeClientsRepository()
        }
    }

    private suspend fun initializeE1SecteurDeClientsRepository() {
        try {
            loadDepuitRoom()
        } catch (e: Exception) {
            // Exception handled silently
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase.e1SecteurDeClientsDao().getAll()
                } catch (e: Exception) {
                    emptyList()
                }

                withContext(Dispatchers.Main) {
                    listCollected.clear()
                    if (dataList.isNotEmpty()) {
                        listCollected.addAll(dataList)
                    }
                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                    collectDepuitRoom()
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    private fun collectDepuitRoom() {
        repositoryScope.launch {
            mainDao.getAllFlow().collectLatest { roomData ->
                withContext(Dispatchers.Main) {
                    listCollected.clear()
                    listCollected.addAll(roomData)
                }
                // Then check consistency with Firebase
                checkDataConsistencyWithFireBase()
            }
        }
    }

    private suspend fun checkDataConsistencyWithFireBase() {
        try {
            val roomCount = withContext(Dispatchers.IO) {
                try {
                    appDatabase.e1SecteurDeClientsDao().getCount()
                } catch (e: Exception) {
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = E1SecteurDeClientsRepository.sonDataBaseRef.get()
                    Tasks.await(task)
                }
            } catch (e: Exception) {
                null
            }

            val firebaseCount = firebaseSnapshot?.childrenCount?.toInt() ?: 0

            if (roomCount != firebaseCount || roomCount == 0) {
                if (firebaseCount > 0) {
                    importDeFireBaseAuRoom(firebaseSnapshot)
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

    private suspend fun importDeFireBaseAuRoom(snapshot: DataSnapshot?) {
        try {
            if (snapshot == null) return

            val updatedList = mutableListOf<E1SecteurDeClients>()
            for (dataSnapshot in snapshot.children) {
                val data = dataSnapshot.getValue(E1SecteurDeClients::class.java)
                data?.let {
                    updatedList.add(it)
                }
            }

            if (updatedList.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    appDatabase.e1SecteurDeClientsDao().deleteAll()
                    appDatabase.e1SecteurDeClientsDao().insertAll(updatedList)
                }
            }
        } catch (e: Exception) {
            // Exception handled silently
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
            // Exception handled silently
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

                            // Update both Room and UI state
                            repositoryScope.launch(Dispatchers.IO) {
                                try {
                                    appDatabase.e1SecteurDeClientsDao().deleteAll()
                                    appDatabase.e1SecteurDeClientsDao().insertAll(updatedList)
                                } catch (e: Exception) {
                                    // Exception handled silently
                                }
                            }
                        } catch (e: Exception) {
                            // Exception handled silently
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Firebase listener cancelled - no logging
                    }
                }
                M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {
                E1SecteurDeClientsRepository.sonDataBaseRef.addValueEventListener(
                    flowValueEventListener!!
                )
                }
                isFlowListenerActive.set(true)
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    E1SecteurDeClientsRepository.sonDataBaseRef.removeEventListener(
                        flowValueEventListener!!
                    )
                } catch (e: Exception) {
                    // Exception handled silently
                } finally {
                    flowValueEventListener = null
                    isFlowListenerActive.set(false)
                }
            }
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            if (isListenerActive.get() && valueEventListener != null) {
                try {
                    E1SecteurDeClientsRepository.sonDataBaseRef.removeEventListener(
                        valueEventListener!!
                    )
                } catch (e: Exception) {
                    // Exception handled silently
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun insert(updatedSecteur: E1SecteurDeClients) {
        repositoryScope.launch {
            mainDao.insert(updatedSecteur)
        }
    }

    override suspend fun insertAvecRetureNewVid(secteur1: E1SecteurDeClients): Long {
        return withContext(Dispatchers.IO) {
            mainDao.insertAvecRetureNewVid(secteur1)
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
