package Z_CodePartageEntreApps.Model.BProto_ClientsDataBaseRepo.Repository

import Z_CodePartageEntreApps.Model.BProto_ClientsDataBase
import Z_CodePartageEntreApps.Model.BProto_ClientsDataBaseRepo.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

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
    }

    override fun load() {
        // Simple load implementation from Firebase reference without using a data listener
        try {
            progressRepo.value = 0f
            modelDatas.clear()

            // Get data synchronously - Firebase will use the local cache due to keepSynced(true)
            val localData = BProto_ClientsDataBaseRepository.caReference.get().result

            localData?.let { snapshot ->
                for (dataSnapshot in snapshot.children) {
                    val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                    clientData?.let {
                        modelDatas.add(it)
                    }
                }
            }

            initialDataLoaded = true
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            // Silently handle exception
            progressRepo.value = 0f
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
            }
        }
    }



    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            // Get reference to the specific product
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            // Optional: Handle unexpected error
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) return

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
                    // Silently handle exception
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
