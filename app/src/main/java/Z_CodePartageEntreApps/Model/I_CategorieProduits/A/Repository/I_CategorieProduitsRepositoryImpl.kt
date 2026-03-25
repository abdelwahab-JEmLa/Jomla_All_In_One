package Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
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

class I_CategorieProduitsRepositoryImpl(
    private val appDatabase: AppDatabase
) : I_CategorieProduitsRepository {
    private val TAG = "I_CategorieProduits"

    override var modelDatas: SnapshotStateList<I_CategorieProduits> = mutableStateListOf()
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
        Log.d(TAG, "Repository initialization started")
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        try {
            Log.d(TAG, "Starting repository initialization")
            loadDepuitRoom() // Always load from Room first for faster UI response
            checkDataConsistency() // Then checkADD_1_4_PeriodeVent and upsertLenceCommandeRepoGroupedProtoAvantJuin3 if necessary
            Log.d(TAG, "Repository initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize repository: ${e.message}", e)
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            Log.d(TAG, "Loading data from Room database")
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val clientsList = appDatabase.I_CategorieProduitsDao().getAll()
                Log.d(TAG, "Room data loaded: ${clientsList.size} records found")

                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (clientsList.isNotEmpty()) {
                        modelDatas.addAll(clientsList)
                        Log.d(TAG, "Added ${clientsList.size} categories to modelDatas")
                    } else {
                        Log.w(TAG, "No category data found in Room database")
                    }
                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Error loading data from Room: ${e.message}", e)
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            Log.d(TAG, "Checking data consistency between Room and Firebase")
            val roomCount = withContext(Dispatchers.IO) {
                val count = appDatabase.I_CategorieProduitsDao().getCount()
                Log.d(TAG, "Room database category count: $count")
                count
            }

            val firebaseSnapshot = withContext(Dispatchers.IO) {
                Log.d(TAG, "Fetching data from Firebase for comparison")
                val task = I_CategorieProduitsRepository.caReference.get()
                Tasks.await(task)
            }

            val firebaseCount = firebaseSnapshot.childrenCount.toInt()
            Log.d(TAG, "Firebase category count: $firebaseCount")

            if (roomCount != firebaseCount || roomCount == 0) {
                Log.w(TAG, "Data inconsistency detected: Room=$roomCount, Firebase=$firebaseCount - Syncing from Firebase")
                importDeFireBaseAuRoom(repositoryScope)
            } else {
                Log.d(TAG, "Data consistency verified between Room and Firebase")
            }

            // RepositorysMainSetter up listener after data consistency checkADD_1_4_PeriodeVent
            withContext(Dispatchers.Main) {
                setUpDataChangeListener()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking data consistency: ${e.message}", e)
            // RepositorysMainSetter up listener even if consistency checkADD_1_4_PeriodeVent fails
            withContext(Dispatchers.Main) {
                setUpDataChangeListener()
                Log.d(TAG, "Setting up data change listener despite consistency checkADD_1_4_PeriodeVent failure")
            }
        }
    }

    private fun setUpDataChangeListener() {
        synchronized(listenerLock) {
            Log.d(TAG, "Setting up Firebase data change listener")
            // Always remove existing listener first
            removeDataChangeListener()

            // Only proceed if no active listener
            if (!isListenerActive.get()) {
                Log.d(TAG, "Creating new ValueEventListener")
                valueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "Firebase data change detected, processing ${snapshot.childrenCount} records")
                        for (dataSnapshot in snapshot.children) {
                            try {
                                val clientData = dataSnapshot.getValue(I_CategorieProduits::class.java)
                                if (clientData == null) {
                                    Log.w(TAG, "Null data received for key: ${dataSnapshot.key}")
                                    continue
                                }

                                Log.d(TAG, "Processing category ID: ${clientData.id}, Name: ${clientData.nom}")
                                val existingIndex = modelDatas.indexOfFirst { it.id == clientData.id }

                                if (existingIndex != -1) {
                                    Log.d(TAG, "Category already exists in modelDatas at index $existingIndex")
                                } else {
                                    // New client data not in our list
                                    Log.d(TAG, "New category detected, adding to modelDatas: ID=${clientData.id}")

                                    repositoryScope.launch(Dispatchers.Main) {
                                        modelDatas.add(clientData)
                                        Log.d(TAG, "Added to modelDatas: ID=${clientData.id}")
                                    }

                                    repositoryScope.launch {
                                        try {
                                            Log.d(TAG, "Inserting new category into Room: ID=${clientData.id}")
                                            appDatabase.I_CategorieProduitsDao().insert(clientData)
                                            Log.d(TAG, "Successfully inserted into Room: ID=${clientData.id}")
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Failed to upsertEtReturnSonNewVid category into Room: ID=${clientData.id}, Error: ${e.message}", e)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing Firebase data snapshot: ${e.message}", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Firebase data change listener cancelled: ${error.message}", error.toException())
                    }
                }

                // RepositorysMainSetter flag before adding listener
                isListenerActive.set(true)
                M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                I_CategorieProduitsRepository.caReference.addValueEventListener(valueEventListener!!) }
                Log.d(TAG, "Firebase data change listener successfully registered")
            } else {
                Log.w(TAG, "Data change listener already active, not creating add_New new one")
            }
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            valueEventListener?.let {
                try {
                    Log.d(TAG, "Removing Firebase data change listener")
                    I_CategorieProduitsRepository.caReference.removeEventListener(it)
                    Log.d(TAG, "Firebase data change listener successfully removed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing Firebase data change listener: ${e.message}", e)
                }
            }
            valueEventListener = null
            isListenerActive.set(false)
        }
    }


    override fun deleteUnSeulData(data: I_CategorieProduits) {
        try {
            Log.d(TAG, "Deleting category: ID=${data.id}, Name=${data.nom}")

            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
                if (recordIndex != -1) {
                    modelDatas.removeAt(recordIndex)
                    Log.d(TAG, "Removed category from modelDatas: ID=${data.id}")
                } else {
                    Log.w(TAG, "Category not found in modelDatas for deletion: ID=${data.id}")
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Removing category from Firebase: ID=${data.id}")
                    I_CategorieProduitsRepository.caReference.child(data.id.toString()).removeValue().await()
                    Log.d(TAG, "Successfully removed from Firebase: ID=${data.id}")

                    Log.d(TAG, "Deleting category from Room: ID=${data.id}")
                    appDatabase.I_CategorieProduitsDao().delete(data)
                    Log.d(TAG, "Successfully deleted from Room: ID=${data.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting category data: ID=${data.id}, Error: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in upsertUneDataEtReturnVID: ${e.message}", e)
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            Log.d(TAG, "Starting import from Firebase to Room")
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
                Log.d(TAG, "Cleared modelDatas for fresh import")
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Fetching all categories from Firebase")
                    val task = I_CategorieProduitsRepository.caReference.get()
                    val snapshot = Tasks.await(task)
                    Log.d(TAG, "Retrieved ${snapshot.childrenCount} categories from Firebase")

                    Log.d(TAG, "Deleting all categories from Room database")
                    appDatabase.I_CategorieProduitsDao().deleteAll()
                    Log.d(TAG, "Successfully deleted all categories from Room")

                    val clientsList = mutableListOf<I_CategorieProduits>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val clientData = dataSnapshot.getValue(I_CategorieProduits::class.java)
                            if (clientData == null) {
                                Log.w(TAG, "Null data received for key: ${dataSnapshot.key}")
                                continue
                            }

                            Log.d(TAG, "Processing category from Firebase: ID=${clientData.id}, Name=${clientData.nom}")
                            clientsList.add(clientData)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting Firebase data: Key=${dataSnapshot.key}, Error: ${e.message}", e)
                        }
                    }

                    if (clientsList.isNotEmpty()) {
                        Log.d(TAG, "Inserting ${clientsList.size} categories into Room database")
                        try {
                            appDatabase.I_CategorieProduitsDao().insertAll(clientsList)
                            Log.d(TAG, "Successfully inserted ${clientsList.size} categories into Room")
                        } catch (e: Exception) {
                            Log.e(TAG, "Batch upsertEtReturnSonNewVid failed, attempting individual inserts", e)
                            // Try individual inserts if batch fails
                            clientsList.forEachIndexed { index, category ->
                                try {
                                    appDatabase.I_CategorieProduitsDao().insert(category)
                                    Log.d(TAG, "Individual upsertEtReturnSonNewVid success: ${index+1}/${clientsList.size}, ID=${category.id}")
                                } catch (e2: Exception) {
                                    Log.e(TAG, "Failed to upsertEtReturnSonNewVid category: ID=${category.id}, Error: ${e2.message}", e2)
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            modelDatas.addAll(clientsList)
                            Log.d(TAG, "Added ${clientsList.size} categories to modelDatas")
                        }
                    } else {
                        Log.w(TAG, "No categories found in Firebase to import")
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                    Log.d(TAG, "Import from Firebase to Room completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to import from Firebase to Room: ${e.message}", e)
                    progressRepo.value = 0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Exception in importDeFireBaseAuRoom: ${e.message}", e)
        }
    }

    override fun addData(data: I_CategorieProduits) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    I_CategorieProduitsRepository.caReference.child(data.id.toString()).setValue(data).await()
                    appDatabase.I_CategorieProduitsDao().insert(data)
                } catch (e: Exception) {
                    // Log error
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    override fun updateUnSeulData(data: I_CategorieProduits?) {
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
                appDatabase.I_CategorieProduitsDao().insert(data)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private suspend fun firebaseUpdateData(data: I_CategorieProduits) {
        try {
            I_CategorieProduitsRepository.caReference.child(data.id.toString()).setValue(data).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<I_CategorieProduits>) {
        if (isUpdating.getAndSet(true)) {
            return
        }

        try {
            val datasList = datas.toList()

            // First, handle Room database upsertLenceCommandeRepoGroupedProtoAvantJuin3
            withContext(Dispatchers.IO) {
                try {
                    appDatabase.I_CategorieProduitsDao().deleteAll()
                    appDatabase.I_CategorieProduitsDao().insertAll(datasList)
                } catch (e: Exception) {
                    // Log error but continue with Firebase updates
                }
            }

            // Then upsertLenceCommandeRepoGroupedProtoAvantJuin3 Firebase (temporarily remove listener to avoid cycles)
            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener

                try {
                    // Remove listener before batch updates
                    synchronized(listenerLock) {
                        valueEventListener?.let {
                            I_CategorieProduitsRepository.caReference.removeEventListener(it)
                        }
                        valueEventListener = null
                        isListenerActive.set(false)
                    }

                    // Update each record individually outside synchronized block
                    for (data in datas) {
                        I_CategorieProduitsRepository.caReference.child(data.id.toString())
                            .setValue(data).await()
                    }
                } catch (e: Exception) {
                    // Log error
                } finally {
                    // Restore listener
                    synchronized(listenerLock) {
                        // Only restore if not already upsert by another thread
                        if (!isListenerActive.get() && tempListener != null) {
                            valueEventListener = tempListener
                            M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                            I_CategorieProduitsRepository.caReference.addValueEventListener(tempListener)}
                            isListenerActive.set(true)
                        }
                    }
                }
            }

            // Finally upsertLenceCommandeRepoGroupedProtoAvantJuin3 UI
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
