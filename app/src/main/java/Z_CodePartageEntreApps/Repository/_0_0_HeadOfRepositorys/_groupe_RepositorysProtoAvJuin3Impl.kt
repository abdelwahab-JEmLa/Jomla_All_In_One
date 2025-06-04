package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import V.DiviseParSections.App.B2_SectionID9_AtelieModbile.Test.Main.B.Models.C3_BonAchate
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL._1_3_TransactionCommercialDao
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentDao
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur.Extension.DataBase._1_5_VendeurDao
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
 *     GroupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3 = koinInject()
 */
class _groupe_RepositorysProtoAvJuin3Impl(

    val appDatabase: AppDatabase,

    private val repo_1_1_CouleurAcheteOperation: _1_1_CouleurAcheteOperation_Repository,
    private val repo_1_2_ProduitAcheteOperation: _1_2_ProduitAcheteOperation_Repository,
    private val repo_1_3_TransactionCommercial: C3_BonAchate_Repository,
    private val _1_4_Repository: _1_4_PeriodeVent_Repository,
    private val _1_5_Repository: _1_5_Vendeur_Repository,

    private val _2_1_Repository: _2_1_ProduitsDataBase_Repository,
    private val _2_2_Repository: _3_ClientsDataBase_Repository,
    private val _4_CouleurOperationCommand_Repository: _4_CouleurOperationCommand_Repository,
    private val e1SecteurDeClientsRepository: E1SecteurDeClientsRepository,
) : GroupeRepositorysProtoAvJuin3 {

    private val TAG = GroupeRepositorysProtoAvJuin3.TAG
    private val activeId_1_3_BonAchat = MutableStateFlow<Long>(-1L)
    override var repositorys_Model: _0_0_HeadOfRepositorys_Model = _0_0_HeadOfRepositorys_Model(
        repo_1_1_CouleurAcheteOperation,
        repo_1_2_ProduitAcheteOperation,
        repo_1_3_TransactionCommercial,
        activeId_1_3_BonAchat,
        _1_4_Repository,
        _1_5_Repository,

        _2_1_Repository,
        _2_2_Repository,

        _4_CouleurOperationCommand_Repository,
        e1SecteurDeClientsRepository  // Add this parameter here
    )

    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var initialDataLoaded = false
    private var lastUpdateTimestamp: Long = 0L
    private var isFlowListenerActive = false


    // In the head repository's init block
    init {
        repositoryScope.launch {
            initialize_0_0_HeadOfRepositoryRepository()

            // Ensure all child repositories are initialized
            repo_1_1_CouleurAcheteOperation.ensureDataIsInitialized()
            repo_1_2_ProduitAcheteOperation.ensureDataIsInitialized()
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

    override fun <T> deleteData(
        data: T,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        try {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    when (data) {
                        is C3_BonAchate -> processDeleteOperation(
                            data = data,
                            databaseDao = appDatabase._1_3_TransactionCommercialDao(),
                            snapshotList = repo_1_3_TransactionCommercial.modelDatasSnapList,
                            databaseRef = C3_BonAchate_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.fireBaseKeyID_1_3_TransactionCommercial },
                            onSuccess = onSuccess,
                            onError = onError
                        )

                        is _1_2_ProduitAcheteOperation -> processDeleteOperation(
                            data = data,
                            databaseDao = appDatabase._1_2_ProduitAcheteOperationDao(),
                            snapshotList = repo_1_2_ProduitAcheteOperation.modelDatasSnapList,
                            databaseRef = _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.fireBaseKeyID },
                            onSuccess = onSuccess,
                            onError = onError
                        )

                        is _1_4_PeriodeVent -> processDeleteOperation(
                            data = data,
                            databaseDao = appDatabase._1_4_PeriodeVentDao(),
                            snapshotList = _1_4_Repository.modelDatasSnapList,
                            databaseRef = _1_4_PeriodeVent_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.fireBaseKeyID_1_4_PeriodeVent },
                            onSuccess = onSuccess,
                            onError = onError
                        )

                        is _1_5_Vendeur -> processDeleteOperation(
                            data = data,
                            databaseDao = appDatabase._1_5_VendeurDao(),
                            snapshotList = _1_5_Repository.modelDatasSnapList,
                            databaseRef = _1_5_Vendeur_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.vid.toString() },
                            onSuccess = onSuccess,
                            onError = onError
                        )

                        else -> {
                            onError(IllegalArgumentException("Unsupported data type for deletion"))
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting data: ${e.message}")
                    Log.e(TAG, "Data: $data")
                    Log.e(TAG, "Stack trace: ", e)
                    onError(e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in deleteData: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            onError(e)
        }
    }

    private suspend inline fun <reified T> processDeleteOperation(
        data: T,
        databaseDao: Any,
        snapshotList: MutableList<T>,
        databaseRef: DatabaseReference,
        crossinline getFirebaseKey: (T) -> String,
        noinline onSuccess: () -> Unit,
        noinline onError: (Exception) -> Unit,
    ) where T : Any {
        try {
            // Access the vid field with proper accessibility
            val vidField = data.javaClass.getDeclaredField("vid").apply {
                isAccessible = true  // Make field accessible
            }

            val currentVid = vidField.getLong(data)
            Log.d(TAG, "Processing deletion of ${data.javaClass.simpleName} with VID: $currentVid")

            if (currentVid <= 0) {
                Log.e(TAG, "Cannot delete entity with invalid VID: $currentVid")
                onError(IllegalArgumentException("Cannot delete entity with invalid VID"))
                return
            }

            // Delete from Room database
            when (databaseDao) {
                is _1_3_TransactionCommercialDao -> databaseDao.delete(data as C3_BonAchate)
                is _1_2_ProduitAcheteOperationDao -> databaseDao.delete(data as _1_2_ProduitAcheteOperation)
                is _1_4_PeriodeVentDao -> databaseDao.delete(data as _1_4_PeriodeVent)
                is _1_5_VendeurDao -> databaseDao.delete(data as _1_5_Vendeur)
                else -> {
                    Log.e(
                        TAG,
                        "Unsupported DAO type for deletion: ${databaseDao.javaClass.simpleName}"
                    )
                    onError(IllegalArgumentException("Unsupported DAO type for deletion"))
                    return
                }
            }

            // Delete from Firebase
            val firebaseKey = getFirebaseKey(data)
            Log.d(TAG, "Deleting from Firebase with key: $firebaseKey")
            databaseRef.child(firebaseKey).removeValue().await()

            // Remove from snapshot list
            withContext(Dispatchers.Main) {
                val index = snapshotList.indexOfFirst {
                    val itemVidField =
                        it.javaClass.getDeclaredField("vid").apply { isAccessible = true }
                    itemVidField.getLong(it) == currentVid
                }
                if (index >= 0) {
                    Log.d(TAG, "Removed item at index $index from snapshot list")
                    snapshotList.removeAt(index)
                } else {
                    Log.d(TAG, "Item not found in snapshot list, may have been previously removed")
                }
            }

            // Call success callback
            onSuccess()

        } catch (e: Exception) {
            Log.e(TAG, "Error in processDeleteOperation: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            onError(e)
        }
    }

    override fun <DataBase> upsertUneDataEtReturnVID(
        data: DataBase,
        onSuccess: (Long) -> Unit,
    ): Unit {
        try {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    when (data) {
                        is C3_BonAchate -> processUpsertOperation(
                            data = data,
                            databaseDao = appDatabase._1_3_TransactionCommercialDao(),
                            snapshotList = repo_1_3_TransactionCommercial.modelDatasSnapList,
                            databaseRef = C3_BonAchate_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.fireBaseKeyID_1_3_TransactionCommercial },
                            onSuccess = { resultVid ->
                                Log.d(
                                    TAG,
                                    "Upsert completed for C3_BonAchate with VID: $resultVid"
                                )
                                if (resultVid <= 0) {
                                    Log.e(
                                        TAG,
                                        "No VID increment occurred. Check database insertion or key generation."
                                    )
                                }
                                onSuccess(resultVid)
                            }
                        )

                        is _1_2_ProduitAcheteOperation -> processUpsertOperation(
                            data = data,
                            databaseDao = appDatabase._1_2_ProduitAcheteOperationDao(),
                            snapshotList = repo_1_2_ProduitAcheteOperation.modelDatasSnapList,
                            databaseRef = _1_2_ProduitAcheteOperation_Repository.sonDataBaseRef,
                            getFirebaseKey = { it.fireBaseKeyID },
                            onSuccess = { resultVid ->
                                Log.d(
                                    TAG,
                                    "Upsert completed for _1_2_ProduitAcheteOperation with VID: $resultVid"
                                )
                                if (resultVid <= 0) {
                                    Log.e(
                                        TAG,
                                        "No VID increment occurred. Check database insertion or key generation."
                                    )
                                }
                                onSuccess(resultVid)
                            }
                        )

                        else -> {
                            onSuccess(-1L)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error upserting data: ${e.message}")
                    Log.e(TAG, "Data: $data")
                    // Additional logging for debugging the VID issue
                    Log.e(TAG, "Stack trace: ", e)
                    onSuccess(-1L)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertUneDataEtReturnVID: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            onSuccess(-1L)
        }
    }

    private suspend inline fun <reified DataBase> processUpsertOperation(
        data: DataBase,
        databaseDao: Any,
        snapshotList: MutableList<DataBase>,
        databaseRef: DatabaseReference,
        crossinline getFirebaseKey: (DataBase) -> String,
        onSuccess: (Long) -> Unit,
    ) where DataBase : Any {
        // Create proper copy based on data type
        val dataToUpsert = when (data) {
            is C3_BonAchate -> data.copy() as DataBase
            is _1_2_ProduitAcheteOperation -> data.copy() as DataBase
            else -> data // Fallback to original object if not a known data class
        }

        // Access the vid field with proper accessibility
        val vidField = dataToUpsert.javaClass.getDeclaredField("vid").apply {
            isAccessible = true  // Make field accessible
        }

        val currentVid = vidField.getLong(dataToUpsert)
        Log.d(TAG, "Processing ${dataToUpsert.javaClass.simpleName} with current VID: $currentVid")

        val vid = when {
            currentVid > 0 -> {
                // Log that we're updating an existing record
                Log.d(TAG, "Updating existing record with VID: $currentVid")

                // Update existing data
                when (databaseDao) {
                    is _1_3_TransactionCommercialDao -> {
                        val result =
                            databaseDao.insertAvecRetureNewVid(dataToUpsert as C3_BonAchate)
                        Log.d(TAG, "Update result for C3_BonAchate: $result")
                        result
                    }

                    is _1_2_ProduitAcheteOperationDao -> {
                        val result =
                            databaseDao.insertAvecRetureNewVid(dataToUpsert as _1_2_ProduitAcheteOperation)
                        Log.d(TAG, "Update result for _1_2_ProduitAcheteOperation: $result")
                        result
                    }

                    else -> {
                        Log.e(TAG, "Unsupported DAO type: ${databaseDao.javaClass.simpleName}")
                        -1L
                    }
                }

                withContext(Dispatchers.Main) {
                    val index = snapshotList.indexOfFirst {
                        val itemVidField =
                            it.javaClass.getDeclaredField("vid").apply { isAccessible = true }
                        itemVidField.getLong(it) == currentVid
                    }
                    if (index >= 0) {
                        Log.d(TAG, "Updated item at index $index in snapshot list")
                        snapshotList[index] = dataToUpsert
                    } else {
                        Log.d(TAG, "Item not found in snapshot list, adding as new")
                        snapshotList.add(dataToUpsert)
                    }
                }

                // Update in Firebase
                Log.d(TAG, "Updating in Firebase with key: ${getFirebaseKey(dataToUpsert)}")
                databaseRef.child(getFirebaseKey(dataToUpsert))
                    .setValue(dataToUpsert).await()

                // Return existing vid
                currentVid
            }

            else -> {
                // Log that we're inserting a new record
                Log.d(TAG, "Inserting new record")

                // Insert as new
                val newVid = when (databaseDao) {
                    is _1_3_TransactionCommercialDao -> {
                        val result =
                            databaseDao.insertAvecRetureNewVid(dataToUpsert as C3_BonAchate)
                        Log.d(TAG, "New VID for C3_BonAchate: $result")
                        result
                    }

                    is _1_2_ProduitAcheteOperationDao -> {
                        val result =
                            databaseDao.insertAvecRetureNewVid(dataToUpsert as _1_2_ProduitAcheteOperation)
                        Log.d(TAG, "New VID for _1_2_ProduitAcheteOperation: $result")
                        result
                    }

                    else -> {
                        Log.e(TAG, "Unsupported DAO type: ${databaseDao.javaClass.simpleName}")
                        -1L
                    }
                }

                if (newVid <= 0) {
                    Log.e(
                        TAG,
                        "Failed to generate new VID. Check database insertion or key generation logic."
                    )
                }

                // Set the new vid on the object with proper accessibility
                Log.d(TAG, "Setting new VID on object: $newVid")
                vidField.set(dataToUpsert, newVid)

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Adding new item to snapshot list")
                    snapshotList.add(dataToUpsert)
                }

                // Update Firebase with the new vid
                Log.d(TAG, "Adding to Firebase with key: ${getFirebaseKey(dataToUpsert)}")
                databaseRef.child(getFirebaseKey(dataToUpsert))
                    .setValue(dataToUpsert).await()

                newVid
            }
        }

        onSuccess(vid)
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
                        _1_5_Vendeur_Repository.sonDataBaseRef.child(dataToUpsert.vid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the existing vid
                        onSuccess(dataToUpsert.vid)
                    } else {
                        // If no valid vid, upsertEtReturnSonNewVid as new (same as addDataAndReturneItVID)
                        val newVid =
                            appDatabase._1_5_VendeurDao().insertAvecRetureNewVid(dataToUpsert)

                        // Update the object with the new vid
                        dataToUpsert.vid = newVid

                        withContext(Dispatchers.Main) {
                            _1_5_Repository.modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase with the new vid
                        _1_5_Vendeur_Repository.sonDataBaseRef.child(newVid.toString())
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

                        _1_4_PeriodeVent_Repository.sonDataBaseRef.child(dataToUpsert.fireBaseKeyID_1_4_PeriodeVent)
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
                        _1_4_PeriodeVent_Repository.sonDataBaseRef.child(dataToUpsert.fireBaseKeyID_1_4_PeriodeVent)
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
                repositoryScope.launch { repo_1_1_CouleurAcheteOperation.ensureDataIsInitialized() },
                repositoryScope.launch { repo_1_2_ProduitAcheteOperation.ensureDataIsInitialized() },
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
                    _1_1_CouleurAcheteOperation_Repository = repo_1_1_CouleurAcheteOperation,
                    repositoryC2_ProduitAcheteOperation = repo_1_2_ProduitAcheteOperation,
                    c3_BonAchate_Repository = repo_1_3_TransactionCommercial,
                    activeVId_C3_BonAchate_Repository = activeId_1_3_BonAchat,
                    repository_1_4_PeriodeVent = _1_4_Repository,
                    repository_1_5_Vendeur = _1_5_Repository,

                    _2_1_ProduitsDataBase_Repository = _2_1_Repository,
                    repository_3_ClientsDataBase = _2_2_Repository,
                    _4_CouleurOperationCommand_Repository = _4_CouleurOperationCommand_Repository,
                    e1SecteurDeClientsRepository = e1SecteurDeClientsRepository // Pass the parameter here
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
                repo_1_1_CouleurAcheteOperation.progressRepo,
                repo_1_2_ProduitAcheteOperation.progressRepo,
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

                // Check if loading is complete (progress = 1.0f)
                if (combinedProgress >= 1.0f && !hasCompletedOnce) {
                    hasCompletedOnce = true
                    onComplete()
                }
            }
        } catch (e: Exception) {
            isFlowListenerActive = false
            Log.e(TAG, "Error tracking progress: ${e.message}")
        }
    }


}
