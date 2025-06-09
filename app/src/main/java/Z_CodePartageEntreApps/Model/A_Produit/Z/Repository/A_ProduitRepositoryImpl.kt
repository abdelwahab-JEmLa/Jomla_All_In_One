package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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

class A_ProduitRepositoryImpl(
    private val appDatabase: AppDatabase
) : A_ProduitRepository {
    private val TAG = "A_Produit"

    override var modelDatas: SnapshotStateList<A_Produit> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    // Use AtomicBoolean for thread safety
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

    init {
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()
        } catch (e: Exception) {
            // Log error
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = appDatabase.a_ProduiteDao().getAll()
                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (dataList.isNotEmpty()) {
                        modelDatas.addAll(dataList)
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
                appDatabase.a_ProduiteDao().getCount()
            }
            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = A_ProduitRepository.sonDataBaseRef.get()
                Tasks.await(task)
            }
            val firebaseCount = firebaseSnapshot.childrenCount.toInt()

            if (roomCount != firebaseCount || roomCount == 0) {
                importDeFireBaseAuRoom(repositoryScope)
            }

            // Set up listener after data consistency checkADD_1_4_PeriodeVent
            withContext(Dispatchers.Main) {
                setUpFlowDataListener()
            }
        } catch (e: Exception) {
            // Set up listener even if consistency checkADD_1_4_PeriodeVent fails
            withContext(Dispatchers.Main) {
                setUpFlowDataListener()
            }
            // Log error
        }
    }


    private fun setUpFlowDataListener() {
        synchronized(flowListenerLock) {
            // Remove existing listener if any
            removeFlowDataListener()

            // Only proceed if no active listener
            if (!isFlowListenerActive.get()) {
                flowValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // If there are any items in the flow upsertLenceCommandeRepoGroupedProtoAvanJuin3 list, process them
                        if (snapshot.exists() && snapshot.childrenCount > 0) {
                            // Process flow updates directly here instead of calling another function
                            repositoryScope.launch {
                                try {
                                    for (dataSnapshot in snapshot.children) {
                                        val productId = dataSnapshot.getValue(Long::class.java)
                                        productId?.let { id ->
                                            // Fetch the updated product data
                                            val productSnapshot = withContext(Dispatchers.IO) {
                                                val task = A_ProduitRepository.sonDataBaseRef.child(id.toString()).get()
                                                Tasks.await(task)
                                            }

                                            val updatedProduct = productSnapshot.getValue(A_Produit::class.java)
                                            updatedProduct?.let { product ->
                                                updateUnSeulData(product)
                                            }

                                            // Remove this ID from the flow upsertLenceCommandeRepoGroupedProtoAvanJuin3 list after processing
                                            withContext(Dispatchers.IO) {
                                                A_ProduitRepository.iDsDatasFlowUpdateRef.child(dataSnapshot.key!!).removeValue()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Log error
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error
                        isFlowListenerActive.set(false)  // Reset flag on cancellation
                    }
                }

                // Set flag before adding listener
                isFlowListenerActive.set(true)
                try {
                    A_ProduitRepository.iDsDatasFlowUpdateRef.addValueEventListener(flowValueEventListener!!)
                } catch (e: Exception) {
                    // If adding the listener fails, reset the flag
                    isFlowListenerActive.set(false)
                    flowValueEventListener = null
                    // Log error
                }
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    A_ProduitRepository.iDsDatasFlowUpdateRef.removeEventListener(flowValueEventListener!!)
                } catch (e: Exception) {
                    // Log error but continue
                } finally {
                    // Always reset these values, even if an exception occurs
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
                    A_ProduitRepository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    // Log error but continue
                } finally {
                    // Always reset these values, even if an exception occurs
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }


    override fun deleteUnSeulData(data: A_Produit) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
                if (recordIndex != -1) {
                    modelDatas.removeAt(recordIndex)
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).removeValue().await()
                    appDatabase.a_ProduiteDao().delete(data)
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
                    val task = A_ProduitRepository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)
                    appDatabase.a_ProduiteDao().deleteAll()
                    val dataList = mutableListOf<A_Produit>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(A_Produit::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            // Log error
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        appDatabase.a_ProduiteDao().insertAll(dataList)
                        withContext(Dispatchers.Main) {
                            modelDatas.addAll(dataList)
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

    override fun addData(data: A_Produit) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
                    appDatabase.a_ProduiteDao().insert(data)
                } catch (e: Exception) {
                    // Log error
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    override fun updateUnSeulData(data: A_Produit) {
        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                appDatabase.a_ProduiteDao().insert(data)
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private suspend fun firebaseUpdateData(data: A_Produit) {
        try {
            A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<A_Produit>) {
        if (isUpdating.getAndSet(true)) {
            return
        }

        try {
            val datasList = datas.toList()

            // First, handle Room database upsertLenceCommandeRepoGroupedProtoAvanJuin3
            withContext(Dispatchers.IO) {
                try {
                    appDatabase.a_ProduiteDao().deleteAll()
                    appDatabase.a_ProduiteDao().insertAll(datasList)
                } catch (e: Exception) {
                    // Log error but continue with Firebase updates
                }
            }

            // Then upsertLenceCommandeRepoGroupedProtoAvanJuin3 Firebase (temporarily remove listener to avoid cycles)
            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener
                val tempFlowListener = flowValueEventListener

                try {
                    // Remove listeners before batch updates
                    synchronized(listenerLock) {
                        valueEventListener?.let {
                            A_ProduitRepository.sonDataBaseRef.removeEventListener(it)
                        }
                        valueEventListener = null
                        isListenerActive.set(false)
                    }

                    synchronized(flowListenerLock) {
                        flowValueEventListener?.let {
                            A_ProduitRepository.iDsDatasFlowUpdateRef.removeEventListener(it)
                        }
                        flowValueEventListener = null
                        isFlowListenerActive.set(false)
                    }

                    // Use the batch upsertLenceCommandeRepoGroupedProtoAvanJuin3 method instead of individual updates
                    batchFireBaseSet(datasList)

                } catch (e: Exception) {
                    // Log error
                } finally {
                    // Restore listeners
                    synchronized(listenerLock) {
                        // Only restore if not already set by another thread
                        if (!isListenerActive.get() && tempListener != null) {
                            valueEventListener = tempListener
                            A_ProduitRepository.sonDataBaseRef.addValueEventListener(tempListener)
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        // Only restore if not already set by another thread
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            flowValueEventListener = tempFlowListener
                            A_ProduitRepository.iDsDatasFlowUpdateRef.addValueEventListener(tempFlowListener)
                            isFlowListenerActive.set(true)
                        }
                    }
                }
            }

            // Finally upsertLenceCommandeRepoGroupedProtoAvanJuin3 UI
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

    private fun batchFireBaseSet(datas: List<A_Produit>): Unit {
        try {
            val reference = A_ProduitRepository.sonDataBaseRef
            val batchUpdates = HashMap<String, Any>()

            // Prepare all updates in a single map
            for (data in datas) {
                batchUpdates[data.id.toString()] = data
            }

            // Apply all updates in a single operation
            reference.updateChildren(batchUpdates)
                .addOnSuccessListener {
                    // Success handling can be added here if needed
                }
                .addOnFailureListener { exception ->
                    // Error handling - could log the exception
                }
        } catch (e: Exception) {
            // Log error but continue
        }
    }

    fun cleanup() {
        repositoryScope.launch {
            removeDataChangeListener()
            removeFlowDataListener()
        }
    }

    // Called when the repository owner is destroyed
    fun onDestroy() {
        cleanup()
    }
}
