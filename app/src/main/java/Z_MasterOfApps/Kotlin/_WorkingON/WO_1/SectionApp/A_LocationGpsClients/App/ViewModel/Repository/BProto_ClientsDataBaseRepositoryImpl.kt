package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
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

    override var modelDatas: SnapshotStateList<BProto_ClientsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsBProto_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()
        CoroutineScope(Dispatchers.IO).launch {
            if (true){
                loadDepuitRoom()
            }else {
                loadDepuitFireStore()
            }
        }
    }

    override suspend fun importDeFireBaseAuRoom() {
        try {
            progressRepo.value = 0f
            modelDatas.clear()

            // Load data from Firebase
            withContext(Dispatchers.IO) {
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)

                // First clear the existing data in Room
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
                        // Handle exception
                    }
                }

                // Insert all clients into Room
                appDatabase.bProtoClientsDataBaseDao().insertAll(clientsList)
            }

            initialDataLoaded = true
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0f
            modelDatas.clear()

            withContext(Dispatchers.IO) {
                // Get all clients from Room database
                val clientsList = appDatabase.bProtoClientsDataBaseDao().getAll()

                // Add clients to modelDatas
                modelDatas.addAll(clientsList)
            }

            initialDataLoaded = true
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }


    private suspend fun loadDepuitFireStore() {
        try {
            progressRepo.value = 0f
            modelDatas.clear()

            withContext(Dispatchers.IO) {
                // Get data asynchronously
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            modelDatas.add(it)
                        }
                    } catch (e: Exception) {
                        // Exception handling remains but without logging
                    }
                }
            }

            initialDataLoaded = true
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    override fun addData(data: BProto_ClientsDataBase) {
        // Add to the local data model
        modelDatas.add(data)

        try {
            // Add to Firebase
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)

            // Add to Room database
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase.bProtoClientsDataBaseDao().insert(data)
            }
        } catch (e: Exception) {
            // Exception handling remains but without logging
        }
    }

    override fun checkConnectivity() {
        connectivityMonitor.checkConnectivityAndSync(
            BProto_ClientsDataBaseRepository.caReference,
        )
    }

    override fun updateData(data: BProto_ClientsDataBase?) {
        if (data == null) return

        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            modelDatas[recordIndex] = data

            try {
                // Update in Firebase
                firebaseUpdateData(data)

                // Update in Room database
                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase.bProtoClientsDataBaseDao().insert(data)
                }
            } catch (e: Exception) {
                // Exception handling remains but without logging
            }
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            // Update in Firebase
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)

            // No need to update in Room here as it's already done in updateData
        } catch (e: Exception) {
            // Exception handling remains but without logging
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) {
            return
        }

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            checkConnectivity()

            withContext(Dispatchers.IO) {
                // First clear existing data in Room
                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                // Convert SnapshotStateList to regular List for insertAll
                val datasList = datas.toList()

                // Insert all into Room
                appDatabase.bProtoClientsDataBaseDao().insertAll(datasList)
            }

            // Update Firebase
            datas.forEach { data ->
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
                    processedItems++
                    progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                } catch (e: Exception) {}
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
        } finally {
            isUpdating = false
        }
    }
}
