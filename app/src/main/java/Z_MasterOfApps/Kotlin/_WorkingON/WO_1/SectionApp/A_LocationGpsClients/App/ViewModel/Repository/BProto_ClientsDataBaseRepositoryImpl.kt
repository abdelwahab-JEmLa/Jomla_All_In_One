package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
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
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Exception handling remains but without logging
            }
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            // Get reference to the specific product
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
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

            datas.forEach { data ->
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
                    processedItems++
                    progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                } catch (e: Exception) {
                    // Exception handling remains but without logging
                }
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
