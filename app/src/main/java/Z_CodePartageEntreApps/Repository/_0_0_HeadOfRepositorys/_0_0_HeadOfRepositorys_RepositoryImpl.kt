package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_2_ProduitAcheteOperation
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Extension.Log._0_0_HeadOfRepositoryLogOperationsExtension
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial._1_3_TransactionCommercialDao
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial._1_3_TransactionCommercial_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import android.util.Log
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * way inject
 *
 *     ,
 *     _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject()
 */
class _0_0_HeadOfRepositorys_RepositoryImpl(
    val appDatabase: AppDatabase,

    private val _1_1_Repository: _1_1_CouleurAcheteOperation_Repository,
    private val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    private val repo_1_3_TransactionCommercial: _1_3_TransactionCommercial_Repository,
    private val _1_4_Repository: _1_4_PeriodeVent_Repository,
    private val _1_5_Repository: _1_5_Vendeur_Repository,

    private val _2_1_Repository: _2_1_ProduitsDataBase_Repository,
    private val _2_2_Repository: _3_ClientsDataBase_Repository,
    private val _4_CouleurOperationCommand_Repository: _4_CouleurOperationCommand_Repository,
) : _0_0_HeadOfRepositorys_Repository {
    private val TAG = _0_0_HeadOfRepositorys_Repository.TAG

    // Create a MutableStateFlow for activeId_1_3_BonAchat
    private val activeId_1_3_BonAchat = MutableStateFlow<Long>(-1L)

    override var repositorys_Model: _0_0_HeadOfRepositorys_Model = _0_0_HeadOfRepositorys_Model(
        _1_1_Repository,
        _1_2_ProduitAcheteOperation_Repository,
        repo_1_3_TransactionCommercial,
        activeId_1_3_BonAchat, // Add the missing MutableStateFlow<Long>
        _1_4_Repository,
        _1_5_Repository,

        _2_1_Repository,
        _2_2_Repository,

        _4_CouleurOperationCommand_Repository,
    )
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var initialDataLoaded = false
    private var lastUpdateTimestamp: Long = 0L
    private var isListenerActive = false
    private var isFlowListenerActive = false

    private val logOperations = _0_0_HeadOfRepositoryLogOperationsExtension(this)

    // In the head repository's init block
    init {
        repositoryScope.launch {
            initialize_0_0_HeadOfRepositoryRepository()

            // Ensure all child repositories are initialized
            _1_1_Repository.ensureDataIsInitialized()
            _1_2_ProduitAcheteOperation_Repository.ensureDataIsInitialized()
            repo_1_3_TransactionCommercial.ensureDataIsInitialized()
            _1_4_Repository.ensureDataIsInitialized()
            _1_5_Repository.ensureDataIsInitialized()

            _2_1_Repository.ensureDataIsInitialized()
            _2_2_Repository.ensureDataIsInitialized()
            _4_CouleurOperationCommand_Repository.ensureDataIsInitialized()

            // Start tracking progress afterward
            startProgressTracking() {
            }
        }
    }

    override fun upsertUneDataEtReturnVID_1_5_Vendeur(
        data: _1_5_Vendeur,
        onSuccess: (Long) -> Unit,
    ): Unit {
        try {
            // Create a copy of the data to work with
            val dataToUpsert = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Check if the data already exists (if it has a valid vid)
                    if (dataToUpsert.vid > 0) {
                        // Update existing data
                        appDatabase._1_5_VendeurDao().insert(dataToUpsert)

                        // Update in snapshot list
                        withContext(Dispatchers.Main) {
                            val index =
                                _1_5_Repository.modelDatasSnapList.indexOfFirst { it.vid == dataToUpsert.vid }
                            if (index >= 0) {
                                _1_5_Repository.modelDatasSnapList[index] = dataToUpsert
                            } else {
                                _1_5_Repository.modelDatasSnapList.add(dataToUpsert)
                            }
                        }

                        // Update in Firebase
                        repositorys_Model.databaseReference_1_5_Vendeur.child(dataToUpsert.vid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the existing vid
                        onSuccess(dataToUpsert.vid)
                    } else {
                        // If no valid vid, insert as new (same as addDataAndReturneItVID)
                        val newVid =
                            appDatabase._1_5_VendeurDao().insertAvecRetureNewVid(dataToUpsert)

                        // Update the object with the new vid
                        dataToUpsert.vid = newVid

                        withContext(Dispatchers.Main) {
                            _1_5_Repository.modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase with the new vid
                        repositorys_Model.databaseReference_1_5_Vendeur.child(newVid.toString())
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

    override fun upsertUneDataEtReturnVID_1_4_PeriodeVent(
        data: _1_4_PeriodeVent,
        onSuccess: (Long) -> Unit,
    ): Unit {
        try {
            val dataToUpsert = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    if (dataToUpsert.vid > 0) {
                        appDatabase._1_4_PeriodeVentDao().insert(dataToUpsert)

                        withContext(Dispatchers.Main) {
                            val index =
                                _1_4_Repository.modelDatasSnapList.indexOfFirst { it.vid == dataToUpsert.vid }
                            if (index >= 0) {
                                _1_4_Repository.modelDatasSnapList[index] = dataToUpsert
                            } else {
                                _1_4_Repository.modelDatasSnapList.add(dataToUpsert)
                            }
                        }

                        dataToUpsert.fireBaseKeyID_1_4_PeriodeVent =
                            "${dataToUpsert.vid}->(${dataToUpsert.startDateInString})"

                        repositorys_Model.databaseReference_1_4_PeriodeVent.child(dataToUpsert.fireBaseKeyID_1_4_PeriodeVent)
                            .setValue(dataToUpsert).await()

                        onSuccess(dataToUpsert.vid)
                    } else {
                        val newVid =
                            appDatabase._1_4_PeriodeVentDao().insertAvecRetureNewVid(dataToUpsert)

                        dataToUpsert.vid = newVid

                        dataToUpsert.fireBaseKeyID_1_4_PeriodeVent =
                            "${dataToUpsert.vid}->(${dataToUpsert.startDateInString})"

                        withContext(Dispatchers.Main) {
                            _1_4_Repository.modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase using fireBaseKeyID as the key
                        repositorys_Model.databaseReference_1_4_PeriodeVent.child(dataToUpsert.fireBaseKeyID_1_4_PeriodeVent)
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

    override fun <T> upsertUneDataEtReturnVID(
        data: T,
        onSuccess: (Long) -> Unit,
    ): Unit {
        try {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    when (data) {
                        is _1_3_TransactionCommercial -> processUpsertOperation(
                            data = data,
                            databaseDao = appDatabase._1_3_TransactionCommercialDao(),
                            snapshotList = repo_1_3_TransactionCommercial.modelDatasSnapList,
                            databaseRef = repositorys_Model.databaseReference_1_3_TransactionCommercial,
                            getFirebaseKey = { it.fireBaseKeyID_1_3_TransactionCommercial },
                            onSuccess = onSuccess
                        )

                        is _1_2_ProduitAcheteOperation -> processUpsertOperation(
                            data = data,
                            databaseDao = appDatabase._1_2_ProduitAcheteOperationDao(),
                            snapshotList = _1_2_ProduitAcheteOperation_Repository.modelDatasSnapList,
                            databaseRef = repositorys_Model.databaseReference_1_2_ProduitAcheteOperation,
                            getFirebaseKey = { it.fireBaseKeyID },
                            onSuccess = onSuccess
                        )

                        else -> {
                            onSuccess(-1L)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error upserting data: ${e.message}")
                    Log.e(TAG, "Data: $data")
                    onSuccess(-1L)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertUneDataEtReturnVID_1_5_Vendeur: ${e.message}")
            onSuccess(-1L)
        }
    }

    private suspend inline fun <reified T> processUpsertOperation(
        data: T,
        databaseDao: Any,
        snapshotList: MutableList<T>,
        databaseRef: DatabaseReference,
        crossinline getFirebaseKey: (T) -> String,
        onSuccess: (Long) -> Unit,
    ) where T : Any {
        // Create proper copy based on data type
        val dataToUpsert = when (data) {
            is _1_3_TransactionCommercial -> data.copy() as T
            is _1_2_ProduitAcheteOperation -> data.copy() as T
            else -> data // Fallback to original object if not a known data class
        }

        // Access the vid field with proper accessibility
        val vidField = dataToUpsert.javaClass.getDeclaredField("vid").apply {
            isAccessible = true  // Make field accessible
        }

        val currentVid = vidField.getLong(dataToUpsert)

        val vid = when {
            currentVid > 0 -> {
                // Update existing data
                when (databaseDao) {
                    is _1_3_TransactionCommercialDao -> databaseDao.insert(dataToUpsert as _1_3_TransactionCommercial)
                    is _1_2_ProduitAcheteOperationDao -> databaseDao.insert(dataToUpsert as _1_2_ProduitAcheteOperation)
                }

                withContext(Dispatchers.Main) {
                    val index = snapshotList.indexOfFirst {
                        val itemVidField = it.javaClass.getDeclaredField("vid").apply { isAccessible = true }
                        itemVidField.getLong(it) == currentVid
                    }
                    if (index >= 0) {
                        snapshotList[index] = dataToUpsert
                    } else {
                        snapshotList.add(dataToUpsert)
                    }
                }

                // Update in Firebase
                databaseRef.child(getFirebaseKey(dataToUpsert))
                    .setValue(dataToUpsert).await()

                // Return existing vid
                currentVid
            }

            else -> {
                // Insert as new
                val newVid = when (databaseDao) {
                    is _1_3_TransactionCommercialDao -> databaseDao.insertAvecRetureNewVid(
                        dataToUpsert as _1_3_TransactionCommercial
                    )

                    is _1_2_ProduitAcheteOperationDao -> databaseDao.insertAvecRetureNewVid(
                        dataToUpsert as _1_2_ProduitAcheteOperation
                    )

                    else -> -1L
                }

                // Set the new vid on the object with proper accessibility
                vidField.set(dataToUpsert, newVid)

                withContext(Dispatchers.Main) {
                    snapshotList.add(dataToUpsert)
                }

                // Update Firebase with the new vid
                databaseRef.child(getFirebaseKey(dataToUpsert))
                    .setValue(dataToUpsert).await()

                newVid
            }
        }

        onSuccess(vid)
    }


    override fun notifyDataChanged_1_3_TransactionCommercial_Repository() {
        repositoryScope.launch {
            try {
                // Reload the products database data
                withContext(Dispatchers.IO) {
                    // First, ensure the repository is initialized
                    repo_1_3_TransactionCommercial.ensureDataIsInitialized()

                    // Refresh data from the database
                    val refreshedData = repo_1_3_TransactionCommercial.modelDatasSnapList.toList()

                    // Update the snapshot list
                    withContext(Dispatchers.Main) {
                        // Clear and upsert_1_3_TransactionCommercial on the main thread
                        repo_1_3_TransactionCommercial.modelDatasSnapList.clear()
                        repo_1_3_TransactionCommercial.modelDatasSnapList.addAll(refreshedData)
                    }
                }

                // Log the refresh operation
                Log.d(
                    TAG,
                    "ProduitsDataBase refreshed: ${repo_1_3_TransactionCommercial.modelDatasSnapList.size} items"
                )

                // Notify any observers that may need to upsert_1_3_TransactionCommercial UI based on this change
                // (This will cause connected components to recompose)
                progressRepo.value =
                    progressRepo.value  // Trigger a small upsert_1_3_TransactionCommercial to force recomposition
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error in notifyDataChanged_1_3_TransactionCommercial_Repository: ${e.message}"
                )
            }
        }
    }

    override fun updateActiveIdDe_1_5_Vendeur(id: Long): Unit {
        repositorys_Model.activeIdDe_1_5_Vendeur = id
    }

    override fun notifyDataChanged_2_1_ProduitsDataBase_Repository() {
        repositoryScope.launch {
            try {
                // Reload the products database data
                withContext(Dispatchers.IO) {
                    // First, ensure the repository is initialized
                    _2_1_Repository.ensureDataIsInitialized()

                    // Refresh data from the database
                    val refreshedData = _2_1_Repository.modelDatasSnapList.toList()

                    // Update the snapshot list
                    withContext(Dispatchers.Main) {
                        // Clear and upsert_1_3_TransactionCommercial on the main thread
                        _2_1_Repository.modelDatasSnapList.clear()
                        _2_1_Repository.modelDatasSnapList.addAll(refreshedData)
                    }
                }

                // Log the refresh operation
                Log.d(
                    TAG,
                    "ProduitsDataBase refreshed: ${_2_1_Repository.modelDatasSnapList.size} items"
                )

                // Notify any observers that may need to upsert_1_3_TransactionCommercial UI based on this change
                // (This will cause connected components to recompose)
                progressRepo.value =
                    progressRepo.value  // Trigger a small upsert_1_3_TransactionCommercial to force recomposition
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error in notifyDataChanged_2_1_ProduitsDataBase_Repository: ${e.message}"
                )
            }
        }
    }

    suspend fun ensureDataIsInitialized() {
        try {
            if (!initialDataLoaded) {
                withContext(Dispatchers.IO) {
                    // Wait until data is loaded
                    var timeoutCounter = 0
                    val maxTimeout = 50 // 5 seconds max wait (50 * 100ms)

                    while (!initialDataLoaded && timeoutCounter < maxTimeout) {
                        delay(100)
                        timeoutCounter++

                        if (progressRepo.value >= 0.95f) {
                            // Check if any required data is missing and create it

                            initialDataLoaded = true
                            progressRepo.value = 1.0f
                        }
                    }

                    if (!initialDataLoaded) {
                        Log.w(TAG, "Data initialization timed out, forcing initialization")
                        initialDataLoaded = true
                        progressRepo.value = 1.0f
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ensuring data initialization: ${e.message}")
            // Even if there's an error, try to create initial data
            initialDataLoaded = true
            progressRepo.value = 1.0f
        }
    }

    private suspend fun initialize_0_0_HeadOfRepositoryRepository() {
        try {
            progressRepo.value = 0.1f
            Log.d(TAG, "Starting repository initialization")

            // Initialize all child repositories in parallel for better performance
            val initJobs = listOf(
                repositoryScope.launch { _1_1_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_2_ProduitAcheteOperation_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { repo_1_3_TransactionCommercial.ensureDataIsInitialized() },
                repositoryScope.launch { _1_4_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_5_Repository.ensureDataIsInitialized() },

                repositoryScope.launch { _2_1_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _2_2_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _4_CouleurOperationCommand_Repository.ensureDataIsInitialized() }
            )

            // Wait for all initialization to complete
            initJobs.forEach { it.join() }

            progressRepo.value = 0.5f
            collectRepositorys()

            if (TAG.isNotEmpty()) {
                log()
            }

            Log.d(TAG, "Repository initialization completed")
        } catch (e: Exception) {
            progressRepo.value = 0.1f
            Log.e(TAG, "Error initializing repository: ${e.message}", e)
        }
    }

    private suspend fun collectRepositorys() {
        try {
            progressRepo.value = 0.6f
            withContext(Dispatchers.IO) {
                // Create a repository head with all repositories
                repositorys_Model = _0_0_HeadOfRepositorys_Model(
                    _1_1_CouleurAcheteOperation_Repository = _1_1_Repository,
                    repository_1_2_ProduitAcheteOperation = _1_2_ProduitAcheteOperation_Repository,
                    repository_1_3_TransactionCommercial = repo_1_3_TransactionCommercial,
                    activeId_1_3_BonAchat = activeId_1_3_BonAchat, // Include it here as well
                    repository_1_4_PeriodeVent = _1_4_Repository,
                    repository_1_5_Vendeur = _1_5_Repository,

                    _2_1_ProduitsDataBase_Repository = _2_1_Repository,
                    repository_3_ClientsDataBase = _2_2_Repository,
                    _4_CouleurOperationCommand_Repository = _4_CouleurOperationCommand_Repository,
                )

                // Update progress
                progressRepo.value = 0.8f
                lastUpdateTimestamp = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            progressRepo.value = 0.5f
            Log.e(TAG, "Error collecting repositories: ${e.message}")
        }
    }

    private suspend fun startProgressTracking(onComplete: () -> Unit = {}) {
        isFlowListenerActive = true
        var hasCompletedOnce = false

        try {
            // Use combine with a different syntax
            val combinedFlow = combine(
                _1_1_Repository.progressRepo,
                _1_2_ProduitAcheteOperation_Repository.progressRepo,
                repo_1_3_TransactionCommercial.progressRepo,
                _1_4_Repository.progressRepo,
                _1_5_Repository.progressRepo,

                _2_1_Repository.progressRepo,
                _2_2_Repository.progressRepo,
                _4_CouleurOperationCommand_Repository.progressRepo
            ) { flowValues ->
                // flowValues is an Array<Float> containing all the progress values
                val combinedProgress = flowValues.sum() / flowValues.size.toFloat()

                // Log the combined progress and possible reasons if not complete
                if (combinedProgress < 1.0f) {
                    Log.d(
                        TAG,
                        "Combined progress: ${String.format("%.2f", combinedProgress * 100)}%"
                    )
                    Log.d(TAG, "Possible reasons for incomplete progress:")

                    flowValues.forEachIndexed { index, progress ->
                        if (progress < 1.0f) {
                            val repoName = when (index) {
                                0 -> "_1_1_Repository"
                                1 -> "_1_2_Repository"
                                2 -> "_1_3_Repository"
                                3 -> "_1_4_Repository"
                                4 -> "_1_5_Repository"

                                5 -> "_2_1_Repository"
                                6 -> "_2_2_Repository"
                                7 -> "_4_CouleurOperationCommand_Repository"
                                else -> "Unknown"
                            }
                            Log.d(TAG, "- $repoName incomplete: $progress")
                        }
                    }
                }

                combinedProgress
            }

            combinedFlow.collect { combinedProgress ->
                progressRepo.value = combinedProgress
                log()

                // Check if loading is complete (progress = 1.0f)
                if (combinedProgress >= 1.0f && !hasCompletedOnce) {
                    hasCompletedOnce = true
                    onComplete()
                }
            }
        } catch (e: Exception) {
            isFlowListenerActive = false
            Log.e(TAG, "Error tracking progress: ${e.message}")
            logOperations.logError("startProgressTracking", e)
        }
    }

    fun log() {
        logOperations.log(
            dataCount = 1, // There's only one model in the repositorys_Model
            initialDataLoaded = initialDataLoaded,
            progressValue = progressRepo.value,
            lastUpdateTimestamp = lastUpdateTimestamp,
            isListenerActive = isListenerActive,
            isFlowListenerActive = isFlowListenerActive
        )
    }
}
