package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.clientjetpack.Modules.AppDatabase
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BProto_ClientsDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase // Inject AppDatabase with Koin
) : BProto_ClientsDataBaseRepository {
    val connectivityMonitor = ConnectivityMonitorNewProto(CoroutineScope(Dispatchers.Default))
    private val TAG = "BProto_ClientsDataBase" // Tag for logging

    override var modelDatas: SnapshotStateList<BProto_ClientsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsBProto_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()
    }

    // In BProto_ClientsDataBaseRepositoryImpl.kt
    override fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            Log.d(
                TAG,
                "importDeFireBaseAuRoom: Starting import from Firebase to Room, setting progressRepo to 0"
            )
            progressRepo.value = 0f
            modelDatas.clear()

            // Use the viewModelScope that's passed as parameter
            viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "importDeFireBaseAuRoom: Fetching data from Firebase")
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)
                Log.d(
                    TAG,
                    "importDeFireBaseAuRoom: Firebase returned ${snapshot.childrenCount} records"
                )

                // First clear the existing data in Room
                Log.d(TAG, "importDeFireBaseAuRoom: Clearing existing data in Room database")
                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                val clientsList = mutableListOf<BProto_ClientsDataBase>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            clientsList.add(it)
                            modelDatas.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "importDeFireBaseAuRoom: Error parsing client data", e)
                    }
                }

                // Insert all clients into Room in a single transaction
                if (clientsList.isNotEmpty()) {
                    Log.d(
                        TAG,
                        "importDeFireBaseAuRoom: Inserting ${clientsList.size} clients into Room database"
                    )
                    appDatabase.bProtoClientsDataBaseDao().insertAll(clientsList)
                }

                initialDataLoaded = true
                Log.d(TAG, "importDeFireBaseAuRoom: Import completed, setting progressRepo to 1.0")
                progressRepo.value = 1.0f
                Log.d(
                    TAG,
                    "importDeFireBaseAuRoom: Current progressRepo value: ${progressRepo.value}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "importDeFireBaseAuRoom: Error importing data from Firebase to Room", e)
            progressRepo.value = 0f
            Log.d(TAG, "importDeFireBaseAuRoom: Reset progressRepo to 0 due to error")
        }
    }

    // Dans BProto_ClientsDataBaseRepositoryImpl.kt
    override fun loadDepuitRoom(viewModelScope: CoroutineScope) {
        try {
            Log.d(TAG, "loadDepuitRoom: Started loading data, setting progressRepo to 0")
            progressRepo.value = 0f

            // Ne pas vider modelDatas ici car cela pourrait causer
            // des problèmes de synchronisation avec la composition
            // modelDatas.clear() <- c'est probablement la source du problème

            viewModelScope.launch(Dispatchers.IO) {
                // Get all clients from Room database
                Log.d(TAG, "loadDepuitRoom: Fetching clients from Room database")
                val clientsList = appDatabase.bProtoClientsDataBaseDao().getAll()
                Log.d(TAG, "loadDepuitRoom: Retrieved ${clientsList.size} clients from Room")

                // Important: utilisez withContext(Dispatchers.Main) pour modifier l'état UI
                withContext(Dispatchers.Main) {
                    // Maintenant on peut vider et remplir la liste en toute sécurité
                    modelDatas.clear()
                    modelDatas.addAll(clientsList)
                    Log.d(TAG, "loadDepuitRoom: Added ${modelDatas.size} clients to modelDatas")

                    initialDataLoaded = true
                    Log.d(TAG, "loadDepuitRoom: Setting progressRepo to 1.0")
                    progressRepo.value = 1.0f
                    Log.d(TAG, "loadDepuitRoom: Current progressRepo value: ${progressRepo.value}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "loadDepuitRoom: Error loading data from Room", e)
            progressRepo.value = 0f
            Log.d(TAG, "loadDepuitRoom: Reset progressRepo to 0 due to error")
        }
    }

    private suspend fun loadDepuitFireStore() {
        try {
            Log.d(TAG, "loadDepuitFireStore: Started loading data, setting progressRepo to 0")
            progressRepo.value = 0f
            modelDatas.clear()

            withContext(Dispatchers.IO) {
                // Get data asynchronously
                Log.d(TAG, "loadDepuitFireStore: Fetching data from Firebase")
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)
                Log.d(
                    TAG,
                    "loadDepuitFireStore: Firebase returned ${snapshot.childrenCount} records"
                )

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            modelDatas.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "loadDepuitFireStore: Error parsing client data", e)
                    }
                }
                Log.d(TAG, "loadDepuitFireStore: Parsed ${modelDatas.size} clients")
            }

            initialDataLoaded = true
            Log.d(TAG, "loadDepuitFireStore: Setting progressRepo to 1.0")
            progressRepo.value = 1.0f
            Log.d(TAG, "loadDepuitFireStore: Current progressRepo value: ${progressRepo.value}")
        } catch (e: Exception) {
            Log.e(TAG, "loadDepuitFireStore: Error loading data from Firebase", e)
            progressRepo.value = 0f
            Log.d(TAG, "loadDepuitFireStore: Reset progressRepo to 0 due to error")
        }
    }

    override fun addData(data: BProto_ClientsDataBase) {
        // Add to the local data model
        modelDatas.add(data)

        try {
            // Add to Firebase
            Log.d(TAG, "addData: Adding client ${data.id} to Firebase")
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)

            // Add to Room database
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, "addData: Adding client ${data.id} to Room database")
                appDatabase.bProtoClientsDataBaseDao().insert(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "addData: Error adding data for client ${data.id}", e)
        }
    }

    override fun checkConnectivity() {
        Log.d(TAG, "checkConnectivity: Checking network connectivity")
        connectivityMonitor.checkConnectivityAndSync(
            BProto_ClientsDataBaseRepository.caReference,
        )
    }

    override fun updateData(data: BProto_ClientsDataBase?) {
        if (data == null) {
            Log.w(TAG, "updateData: Null data provided, skipping update")
            return
        }

        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            Log.d(TAG, "updateData: Updating client ${data.id} at index $recordIndex")
            modelDatas[recordIndex] = data

            try {
                // Update in Firebase
                Log.d(TAG, "updateData: Updating client ${data.id} in Firebase")
                firebaseUpdateData(data)

                // Update in Room database
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "updateData: Updating client ${data.id} in Room database")
                    appDatabase.bProtoClientsDataBaseDao().insert(data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateData: Error updating data for client ${data.id}", e)
            }
        } else {
            Log.w(TAG, "updateData: Client ${data.id} not found in modelDatas")
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            // Update in Firebase
            Log.d(TAG, "firebaseUpdateData: Updating client ${data.id} in Firebase")
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)

            // No need to update in Room here as it's already done in updateData
        } catch (e: Exception) {
            Log.e(
                TAG,
                "firebaseUpdateData: Error updating data in Firebase for client ${data.id}",
                e
            )
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) {
            Log.w(TAG, "updateDatas: Update already in progress, skipping")
            return
        }

        try {
            isUpdating = true
            Log.d(
                TAG,
                "updateDatas: Starting batch update of ${datas.size} clients, setting progressRepo to 0"
            )
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            checkConnectivity()

            withContext(Dispatchers.IO) {
                // First clear existing data in Room
                Log.d(TAG, "updateDatas: Clearing existing data in Room database")
                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                // Convert SnapshotStateList to regular List for insertAll
                val datasList = datas.toList()

                // Insert all into Room
                Log.d(TAG, "updateDatas: Inserting ${datasList.size} clients into Room database")
                appDatabase.bProtoClientsDataBaseDao().insertAll(datasList)
            }

            // Update Firebase
            Log.d(TAG, "updateDatas: Starting to update clients in Firebase")
            datas.forEach { data ->
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString())
                        .setValue(data)
                    processedItems++
                    progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    Log.d(
                        TAG,
                        "updateDatas: Updated client ${data.id} in Firebase, progress: ${progressRepo.value}"
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "updateDatas: Error updating client ${data.id} in Firebase", e)
                }
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            Log.d(TAG, "updateDatas: Batch update completed, setting progressRepo to 1.0")
            progressRepo.value = 1.0f
            Log.d(TAG, "updateDatas: Current progressRepo value: ${progressRepo.value}")
        } catch (e: Exception) {
            Log.e(TAG, "updateDatas: Error during batch update", e)
            progressRepo.value = 0f
            Log.d(TAG, "updateDatas: Reset progressRepo to 0 due to error")
        } finally {
            isUpdating = false
            Log.d(TAG, "updateDatas: Update flag reset to false")
        }
    }
}
