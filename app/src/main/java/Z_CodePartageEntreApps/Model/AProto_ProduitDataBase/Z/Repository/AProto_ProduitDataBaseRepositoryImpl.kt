package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.Extension.FirebaseUtils_AProto_ProduitDataBase
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.AppDatabase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class AProto_ProduitDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : AProto_ProduitDataBaseRepository {
    private val TAG = "AProto_ProduitDataBase"

    override var modelDatas: SnapshotStateList<AProto_ProduitDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    // Use AtomicBoolean for thread safety
    private val isUpdating = AtomicBoolean(false)
    private val isListenerActive = AtomicBoolean(false)

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()

    init {
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        try {
            FirebaseUtils_AProto_ProduitDataBase.initializeFirebaseOfflineCapability()
            loadDepuitRoom() // Always load from Room first for faster UI response
            checkDataConsistency() // Then check and update if necessary
        } catch (e: Exception) {
            // Log error
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val clientsList = appDatabase.aProto_ProduitDataBaseDao().getAll()
                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (clientsList.isNotEmpty()) {
                        modelDatas.addAll(clientsList)
                    }
                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            // Log error
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            val roomCount = withContext(Dispatchers.IO) {
                appDatabase.aProto_ProduitDataBaseDao().getCount()
            }
            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = AProto_ProduitDataBaseRepository.caReference.get()
                Tasks.await(task)
            }
            val firebaseCount = firebaseSnapshot.childrenCount.toInt()

            if (roomCount != firebaseCount || roomCount == 0) {
                importDeFireBaseAuRoom(repositoryScope)
            }

            // Set up listener after data consistency check
            withContext(Dispatchers.Main) {
                setUpDataChangeListener()
            }
        } catch (e: Exception) {
            // Set up listener even if consistency check fails
            withContext(Dispatchers.Main) {
                setUpDataChangeListener()
            }
            // Log error
        }
    }

    private fun setUpDataChangeListener() {
        synchronized(listenerLock) {
            // Always remove existing listener first
            removeDataChangeListener()

            // Only proceed if no active listener
            if (!isListenerActive.get()) {
                valueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot in snapshot.children) {
                            try {
                                val clientData = dataSnapshot.getValue(AProto_ProduitDataBase::class.java)
                                clientData?.let { newData ->
                                    val existingIndex = modelDatas.indexOfFirst { it.id == newData.id }
                                    if (existingIndex != -1) {
                                        val existingData = modelDatas[existingIndex]
                                        if (newData.hasRelevantChanges(existingData, newData)) {
                                            repositoryScope.launch {
                                                updateUnSeulData(newData)
                                            }
                                        } else {
                                            // No relevant changes
                                        }
                                    } else {
                                        // New client data not in our list
                                        repositoryScope.launch(Dispatchers.Main) {
                                            modelDatas.add(newData)
                                        }
                                        repositoryScope.launch {
                                            appDatabase.aProto_ProduitDataBaseDao().insert(newData)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Log error
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error
                    }
                }

                // Set flag before adding listener
                isListenerActive.set(true)
                AProto_ProduitDataBaseRepository.caReference.addValueEventListener(valueEventListener!!)
            }
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            valueEventListener?.let {
                try {
                    AProto_ProduitDataBaseRepository.caReference.removeEventListener(it)
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
            valueEventListener = null
            isListenerActive.set(false)
        }
    }


    override fun deleteUnSeulData(data: AProto_ProduitDataBase) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
                if (recordIndex != -1) {
                    modelDatas.removeAt(recordIndex)
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    AProto_ProduitDataBaseRepository.caReference.child(data.id.toString()).removeValue().await()
                    appDatabase.aProto_ProduitDataBaseDao().delete(data)
                } catch (e: Exception) {
                    // Log error
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val task = AProto_ProduitDataBaseRepository.caReference.get()
                    val snapshot = Tasks.await(task)
                    appDatabase.aProto_ProduitDataBaseDao().deleteAll()
                    val clientsList = mutableListOf<AProto_ProduitDataBase>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val clientData = dataSnapshot.getValue(AProto_ProduitDataBase::class.java)
                            clientData?.let {
                                clientsList.add(it)
                            }
                        } catch (e: Exception) {
                            // Log error
                        }
                    }

                    if (clientsList.isNotEmpty()) {
                        appDatabase.aProto_ProduitDataBaseDao().insertAll(clientsList)
                        withContext(Dispatchers.Main) {
                            modelDatas.addAll(clientsList)
                        }
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    // Log error and ensure progress is reset
                    progressRepo.value = 0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            // Log error
        }
    }

    override fun addData(data: AProto_ProduitDataBase) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    AProto_ProduitDataBaseRepository.caReference.child(data.id.toString()).setValue(data).await()
                    appDatabase.aProto_ProduitDataBaseDao().insert(data)
                } catch (e: Exception) {
                    // Log error
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    override fun updateUnSeulData(data: AProto_ProduitDataBase?) {
        if (data == null) {
            return
        }

        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                firebaseUpdateData(data)
                appDatabase.aProto_ProduitDataBaseDao().insert(data)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private suspend fun firebaseUpdateData(data: AProto_ProduitDataBase) {
        try {
            AProto_ProduitDataBaseRepository.caReference.child(data.id.toString()).setValue(data).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<AProto_ProduitDataBase>) {
        if (isUpdating.getAndSet(true)) {
            return
        }

        try {
            val datasList = datas.toList()

            // First, handle Room database update
            withContext(Dispatchers.IO) {
                try {
                    appDatabase.aProto_ProduitDataBaseDao().deleteAll()
                    appDatabase.aProto_ProduitDataBaseDao().insertAll(datasList)
                } catch (e: Exception) {
                    // Log error but continue with Firebase updates
                }
            }

            // Then update Firebase (temporarily remove listener to avoid cycles)
            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener

                try {
                    // Remove listener before batch updates
                    synchronized(listenerLock) {
                        valueEventListener?.let {
                            AProto_ProduitDataBaseRepository.caReference.removeEventListener(it)
                        }
                        valueEventListener = null
                        isListenerActive.set(false)
                    }

                    // Update each record individually outside synchronized block
                    for (data in datas) {
                        AProto_ProduitDataBaseRepository.caReference.child(data.id.toString())
                            .setValue(data).await()
                    }
                } catch (e: Exception) {
                    // Log error
                } finally {
                    // Restore listener
                    synchronized(listenerLock) {
                        // Only restore if not already set by another thread
                        if (!isListenerActive.get() && tempListener != null) {
                            valueEventListener = tempListener
                            AProto_ProduitDataBaseRepository.caReference.addValueEventListener(tempListener)
                            isListenerActive.set(true)
                        }
                    }
                }
            }

            // Finally update UI
            withContext(Dispatchers.Main) {
                modelDatas.clear()
                modelDatas.addAll(datas)
            }
        } catch (e: Exception) {
            // Log error
        } finally {
            isUpdating.set(false)
        }
    }

    fun cleanup() {
        repositoryScope.launch {
            removeDataChangeListener()
        }
    }

    // Called when the repository owner is destroyed
    fun onDestroy() {
        cleanup()
    }
}
