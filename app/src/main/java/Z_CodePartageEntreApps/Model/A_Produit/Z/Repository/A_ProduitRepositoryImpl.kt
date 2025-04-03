package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Modules.AppDatabase
import android.util.Log
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
    private val TAG = "A_ProduitRepo"

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
        Log.d(TAG, "Repository initialized")
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        try {
            Log.d(TAG, "Starting repository initialization")
            loadDepuitRoom()
            checkDataConsistency()
            Log.d(TAG, "Repository initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize repository", e)
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            Log.d(TAG, "Loading data from Room database")
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = appDatabase.a_ProduiteDao().getAll()
                Log.d(TAG, "Loaded ${dataList.size} products from Room")

                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (dataList.isNotEmpty()) {
                        modelDatas.addAll(dataList)
                        Log.d(TAG, "Added ${dataList.size} products to modelDatas")
                    } else {
                        Log.w(TAG, "No products found in Room database")
                    }
                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                    Log.d(TAG, "Room data loading complete, progress set to 1.0")
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Failed to load data from Room", e)
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            Log.d(TAG, "Checking data consistency between Room and Firebase")
            val roomCount = withContext(Dispatchers.IO) {
                appDatabase.a_ProduiteDao().getCount()
            }
            Log.d(TAG, "Room product count: $roomCount")

            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = A_ProduitRepository.sonDataBaseRef.get()
                Tasks.await(task)
            }
            val firebaseCount = firebaseSnapshot.childrenCount.toInt()
            Log.d(TAG, "Firebase product count: $firebaseCount")

            if (roomCount != firebaseCount || roomCount == 0) {
                Log.w(TAG, "Data inconsistency detected: Room count=$roomCount, Firebase count=$firebaseCount")
                Log.d(TAG, "Starting import from Firebase to Room")
                importDeFireBaseAuRoom(repositoryScope)
            } else {
                Log.d(TAG, "Data consistency verified between Room and Firebase")
            }

            // Set up listener after data consistency check
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Setting up flow data listener")
                setUpFlowDataListener()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during data consistency check", e)
            // Set up listener even if consistency check fails
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Setting up flow data listener despite consistency check failure")
                setUpFlowDataListener()
            }
        }
    }

    private fun setUpFlowDataListener() {
        synchronized(flowListenerLock) {
            Log.d(TAG, "Setting up flow data listener, current status: ${isFlowListenerActive.get()}")
            // Remove existing listener if any
            removeFlowDataListener()

            // Only proceed if no active listener
            if (!isFlowListenerActive.get()) {
                flowValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val updateCount = snapshot.childrenCount
                        Log.d(TAG, "Flow data changed, updates available: $updateCount")

                        // If there are any items in the flow update list, process them
                        if (snapshot.exists() && updateCount > 0) {
                            Log.d(TAG, "Processing $updateCount flow updates")

                            // Process flow updates directly here instead of calling another function
                            repositoryScope.launch {
                                try {
                                    for (dataSnapshot in snapshot.children) {
                                        val productId = dataSnapshot.getValue(Long::class.java)
                                        Log.d(TAG, "Processing flow update for product ID: $productId")

                                        productId?.let { id ->
                                            // Fetch the updated product data
                                            val productSnapshot = withContext(Dispatchers.IO) {
                                                val task = A_ProduitRepository.sonDataBaseRef.child(id.toString()).get()
                                                Tasks.await(task)
                                            }

                                            val updatedProduct = productSnapshot.getValue(A_Produit::class.java)
                                            if (updatedProduct != null) {
                                                Log.d(TAG, "Successfully fetched updated product: ID=${updatedProduct.id}, Name=${updatedProduct.nom}")
                                                updateUnSeulData(updatedProduct)
                                            } else {
                                                Log.w(TAG, "Failed to fetch updated product data for ID: $id")
                                            }

                                            // Remove this ID from the flow update list after processing
                                            withContext(Dispatchers.IO) {
                                                A_ProduitRepository.iDsDatasFlowUpdateRef.child(dataSnapshot.key!!).removeValue()
                                                Log.d(TAG, "Removed processed update from flow update list: ${dataSnapshot.key}")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error processing flow updates", e)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Flow data listener cancelled: ${error.message}", error.toException())
                        isFlowListenerActive.set(false)  // Reset flag on cancellation
                    }
                }

                // Set flag before adding listener
                isFlowListenerActive.set(true)
                try {
                    A_ProduitRepository.iDsDatasFlowUpdateRef.addValueEventListener(flowValueEventListener!!)
                    Log.d(TAG, "Flow data listener successfully added")
                } catch (e: Exception) {
                    // If adding the listener fails, reset the flag
                    isFlowListenerActive.set(false)
                    flowValueEventListener = null
                    Log.e(TAG, "Failed to add flow data listener", e)
                }
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                Log.d(TAG, "Removing flow data listener")
                try {
                    A_ProduitRepository.iDsDatasFlowUpdateRef.removeEventListener(flowValueEventListener!!)
                    Log.d(TAG, "Flow data listener removed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing flow data listener", e)
                } finally {
                    // Always reset these values, even if an exception occurs
                    flowValueEventListener = null
                    isFlowListenerActive.set(false)
                    Log.d(TAG, "Flow data listener reference cleared")
                }
            }
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            if (isListenerActive.get() && valueEventListener != null) {
                Log.d(TAG, "Removing data change listener")
                try {
                    A_ProduitRepository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                    Log.d(TAG, "Data change listener removed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing data change listener", e)
                } finally {
                    // Always reset these values, even if an exception occurs
                    valueEventListener = null
                    isListenerActive.set(false)
                    Log.d(TAG, "Data change listener reference cleared")
                }
            }
        }
    }

    override fun deleteUnSeulData(data: A_Produit) {
        try {
            Log.d(TAG, "Deleting product: ID=${data.id}, Name=${data.nom}")

            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
                if (recordIndex != -1) {
                    modelDatas.removeAt(recordIndex)
                    Log.d(TAG, "Product removed from in-memory model at index $recordIndex")
                } else {
                    Log.w(TAG, "Product not found in in-memory model: ID=${data.id}")
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Removing product from Firebase: ID=${data.id}")
                    A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).removeValue().await()
                    Log.d(TAG, "Product successfully removed from Firebase")

                    Log.d(TAG, "Removing product from Room: ID=${data.id}")
                    appDatabase.a_ProduiteDao().delete(data)
                    Log.d(TAG, "Product successfully removed from Room")
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting product from databases: ID=${data.id}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in deleteUnSeulData", e)
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            Log.d(TAG, "Starting import from Firebase to Room")
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
                Log.d(TAG, "Cleared in-memory model data")
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Fetching all products from Firebase")
                    val task = A_ProduitRepository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)
                    val totalProducts = snapshot.childrenCount
                    Log.d(TAG, "Firebase returned $totalProducts products")

                    Log.d(TAG, "Deleting all products from Room")
                    appDatabase.a_ProduiteDao().deleteAll()
                    Log.d(TAG, "Room database cleared")

                    val dataList = mutableListOf<A_Produit>()
                    var processedCount = 0
                    var errorCount = 0

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(A_Produit::class.java)
                            data?.let {
                                dataList.add(it)
                                processedCount++

                                // Update progress periodically
                                if (processedCount % 10 == 0 || processedCount == totalProducts.toInt()) {
                                    val progress = processedCount.toFloat() / totalProducts.toFloat()
                                    progressRepo.value = progress * 0.9f // Save 10% for final steps
                                    Log.d(TAG, "Import progress: $processedCount/$totalProducts (${progress * 100}%)")
                                }
                            }
                        } catch (e: Exception) {
                            errorCount++
                            Log.e(TAG, "Error parsing product from Firebase", e)
                        }
                    }

                    Log.d(TAG, "Successfully processed $processedCount products with $errorCount errors")

                    if (dataList.isNotEmpty()) {
                        Log.d(TAG, "Inserting ${dataList.size} products into Room")
                        appDatabase.a_ProduiteDao().insertAll(dataList)
                        Log.d(TAG, "Products successfully inserted into Room")

                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "Updating in-memory model with ${dataList.size} products")
                            modelDatas.addAll(dataList)
                            Log.d(TAG, "In-memory model updated")
                        }
                    } else {
                        Log.w(TAG, "No products to insert into Room database")
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                    Log.d(TAG, "Import from Firebase to Room completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error importing from Firebase to Room", e)
                    progressRepo.value = 0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Unexpected error in importDeFireBaseAuRoom", e)
        }
    }

    override fun addData(data: A_Produit) {
        try {
            Log.d(TAG, "Adding new product: ID=${data.id}, Name=${data.nom}")

            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
                Log.d(TAG, "Product added to in-memory model")
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Adding product to Firebase: ID=${data.id}")
                    A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
                    Log.d(TAG, "Product successfully added to Firebase")

                    Log.d(TAG, "Adding product to Room: ID=${data.id}")
                    appDatabase.a_ProduiteDao().insert(data)
                    Log.d(TAG, "Product successfully added to Room")
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding product to databases: ID=${data.id}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in addData", e)
        }
    }

    override fun updateUnSeulData(data: A_Produit?) {
        if (data == null) {
            Log.w(TAG, "Attempted to update null product")
            return
        }

        Log.d(TAG, "Updating product: ID=${data.id}, Name=${data.nom}")

        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas[recordIndex] = data
                Log.d(TAG, "Product updated in in-memory model at index $recordIndex")
            } else {
                Log.w(TAG, "Product not found in in-memory model for update: ID=${data.id}")
                // If not found, add it as a new item
                modelDatas.add(data)
                Log.d(TAG, "Product added to in-memory model as it was not found")
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Updating product in Firebase: ID=${data.id}")
                firebaseUpdateData(data)
                Log.d(TAG, "Product successfully updated in Firebase")

                Log.d(TAG, "Updating product in Room: ID=${data.id}")
                appDatabase.a_ProduiteDao().insert(data)
                Log.d(TAG, "Product successfully updated in Room")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating product in databases: ID=${data.id}", e)
            }
        }
    }

    private suspend fun firebaseUpdateData(data: A_Produit) {
        try {
            Log.d(TAG, "Executing Firebase update for product: ID=${data.id}")
            A_ProduitRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
            Log.d(TAG, "Firebase update completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating product in Firebase: ID=${data.id}", e)
        }
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<A_Produit>) {
        if (isUpdating.getAndSet(true)) {
            Log.w(TAG, "Update already in progress, skipping this update")
            return
        }

        try {
            Log.d(TAG, "Starting batch update of ${datas.size} products")
            val datasList = datas.toList()

            // First, handle Room database update
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Deleting all products from Room")
                    appDatabase.a_ProduiteDao().deleteAll()
                    Log.d(TAG, "Inserting ${datasList.size} products into Room")
                    appDatabase.a_ProduiteDao().insertAll(datasList)
                    Log.d(TAG, "Room database update completed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Room database during batch update", e)
                }
            }

            // Then update Firebase (temporarily remove listener to avoid cycles)
            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener
                val tempFlowListener = flowValueEventListener

                try {
                    // Remove listeners before batch updates
                    synchronized(listenerLock) {
                        if (valueEventListener != null) {
                            Log.d(TAG, "Temporarily removing data change listener for batch update")
                            A_ProduitRepository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                            valueEventListener = null
                            isListenerActive.set(false)
                        }
                    }

                    synchronized(flowListenerLock) {
                        if (flowValueEventListener != null) {
                            Log.d(TAG, "Temporarily removing flow data listener for batch update")
                            A_ProduitRepository.iDsDatasFlowUpdateRef.removeEventListener(flowValueEventListener!!)
                            flowValueEventListener = null
                            isFlowListenerActive.set(false)
                        }
                    }

                    // Use the batch update method instead of individual updates
                    Log.d(TAG, "Starting batch update to Firebase")
                    batchFireBaseSet(datasList)

                } catch (e: Exception) {
                    Log.e(TAG, "Error during Firebase batch update", e)
                } finally {
                    // Restore listeners
                    synchronized(listenerLock) {
                        // Only restore if not already set by another thread
                        if (!isListenerActive.get() && tempListener != null) {
                            Log.d(TAG, "Restoring data change listener after batch update")
                            valueEventListener = tempListener
                            A_ProduitRepository.sonDataBaseRef.addValueEventListener(tempListener)
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        // Only restore if not already set by another thread
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            Log.d(TAG, "Restoring flow data listener after batch update")
                            flowValueEventListener = tempFlowListener
                            A_ProduitRepository.iDsDatasFlowUpdateRef.addValueEventListener(tempFlowListener)
                            isFlowListenerActive.set(true)
                        }
                    }
                }
            }

            // Finally update UI
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Updating in-memory model with ${datas.size} products")
                modelDatas.clear()
                modelDatas.addAll(datas)
                Log.d(TAG, "In-memory model update completed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in updateMultiDatas", e)
        } finally {
            isUpdating.set(false)
            Log.d(TAG, "Batch update completed, unlocked for future updates")
        }
    }

    private fun batchFireBaseSet(datas: List<A_Produit>): Unit {
        try {
            Log.d(TAG, "Executing batch update to Firebase with ${datas.size} products")
            val reference = A_ProduitRepository.sonDataBaseRef
            val batchUpdates = HashMap<String, Any>()

            // Prepare all updates in a single map
            for (data in datas) {
                batchUpdates[data.id.toString()] = data
            }

            // Apply all updates in a single operation
            reference.updateChildren(batchUpdates)
                .addOnSuccessListener {
                    Log.d(TAG, "Firebase batch update completed successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Firebase batch update failed", exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing Firebase batch update", e)
        }
    }

    fun cleanup() {
        Log.d(TAG, "Cleaning up repository resources")
        repositoryScope.launch {
            removeDataChangeListener()
            removeFlowDataListener()
        }
        Log.d(TAG, "Repository cleanup initiated")
    }

    // Called when the repository owner is destroyed
    fun onDestroy() {
        Log.d(TAG, "Repository being destroyed")
        cleanup()
    }
}
