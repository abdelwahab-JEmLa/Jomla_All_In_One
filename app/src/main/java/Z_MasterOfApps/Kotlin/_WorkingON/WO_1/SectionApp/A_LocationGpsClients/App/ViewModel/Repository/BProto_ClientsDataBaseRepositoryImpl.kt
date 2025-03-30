package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BProto_ClientsDataBaseRepositoryImpl(
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
            load()
        }
    }

    private suspend fun load() {
        try {
            Log.d("BProto_ClientsDataBase", "Starting data load...")
            progressRepo.value = 0f
            modelDatas.clear()

            withContext(Dispatchers.IO) {
                // Get data asynchronously
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)

                Log.d("BProto_ClientsDataBase", "Data retrieval: ${snapshot.childrenCount} items found")

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            modelDatas.add(it)
                            Log.d("BProto_ClientsDataBase", "Added client: ${it.id} - ${it.nom}")
                        } ?: Log.w("BProto_ClientsDataBase", "Failed to parse client data for key: ${dataSnapshot.key}")
                    } catch (e: Exception) {
                        Log.e("BProto_ClientsDataBase", "Error processing client data for key: ${dataSnapshot.key}", e)
                    }
                }
            }

            initialDataLoaded = true
            progressRepo.value = 1.0f
            Log.d("BProto_ClientsDataBase", "Load complete. Added ${modelDatas.size} items")
        } catch (e: Exception) {
            Log.e("BProto_ClientsDataBase", "Error loading data", e)
            progressRepo.value = 0f
        }
    }
    override fun addData(data: BProto_ClientsDataBase) {
        // Add to the local data model
        modelDatas.add(data)

        try {
            // Add to Firebase
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
            Log.d("BProto_ClientsDataBase", "Added client to Firebase: ${data.id} - ${data.nom}")
        } catch (e: Exception) {
            // Log the exception
            Log.e("BProto_ClientsDataBase", "Error adding client to Firebase: ${data.id}", e)
        }
    }

    override fun checkConnectivity() {
        Log.d("BProto_ClientsDataBase", "Checking connectivity status")
        connectivityMonitor.checkConnectivityAndSync(
            BProto_ClientsDataBaseRepository.caReference,
        )
    }

    override fun updateData(data: BProto_ClientsDataBase?) {
        if (data == null) return

        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
        Log.d("BProto_ClientsDataBase", "Updating client ${data.id}: found at index $recordIndex")

        if (recordIndex != -1) {
            modelDatas[recordIndex] = data

            try {
                firebaseUpdateData(data)
            } catch (e: Exception) {
                Log.e("BProto_ClientsDataBase", "Error updating client in local model: ${data.id}", e)
            }
        } else {
            Log.w("BProto_ClientsDataBase", "Client not found in local model: ${data.id}")
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            // Get reference to the specific product
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
            Log.d("BProto_ClientsDataBase", "Updated client in Firebase: ${data.id} - ${data.nom}")
        } catch (e: Exception) {
            // Log the error
            Log.e("BProto_ClientsDataBase", "Error updating client in Firebase: ${data.id}", e)
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) {
            Log.d("BProto_ClientsDataBase", "Already updating, skipping this update request")
            return
        }

        try {
            isUpdating = true
            progressRepo.value = 0f
            Log.d("BProto_ClientsDataBase", "Starting batch update of ${datas.size} clients")

            val totalItems = datas.size
            var processedItems = 0

            checkConnectivity()

            datas.forEach { data ->
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
                    processedItems++
                    progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    Log.d("BProto_ClientsDataBase", "Updated client ${data.id} (${processedItems}/$totalItems)")
                } catch (e: Exception) {
                    Log.e("BProto_ClientsDataBase", "Error updating client ${data.id} in batch", e)
                }
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            progressRepo.value = 1.0f
            Log.d("BProto_ClientsDataBase", "Batch update completed successfully")
        } catch (e: Exception) {
            Log.e("BProto_ClientsDataBase", "Error during batch update", e)
            progressRepo.value = 0f
        } finally {
            isUpdating = false
        }
    }
}
