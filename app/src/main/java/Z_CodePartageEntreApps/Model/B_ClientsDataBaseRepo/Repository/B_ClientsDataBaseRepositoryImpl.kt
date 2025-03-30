package Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.Extension.FirebaseUtilsB_ClientsDataBaseNewProto
import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class B_ClientsDataBaseRepositoryImpl(
) : B_ClientsDataBaseRepository{
    override var modelDatas: SnapshotStateList<B_ClientsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val connectivityMonitor = ConnectivityMonitorNewProto(CoroutineScope(Dispatchers.Default))

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsB_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()
        startDatabaseListener()
    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsB_ClientsDataBaseNewProto.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    override fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        connectivityMonitor.checkConnectivityAndSync(
            B_ClientsDataBaseRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            },
        )
    }

    override fun checkConnectivity() {
        connectivityMonitor.checkConnectivityAndSync(
            B_ClientsDataBaseRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            }
        )
    }

    override fun updateData(data: B_ClientsDataBase?) {
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

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<B_ClientsDataBase>, Flow<Float>> {
        checkConnectivityAndSync()
        return Pair(modelDatas.toList(), progressRepo)
    }

    private fun firebaseUpdateData(data: B_ClientsDataBase) {
        try {
            // Get reference to the specific product
            B_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            // Optional: Handle unexpected error
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<B_ClientsDataBase>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()
            checkConnectivity()

            datas.forEach { data ->
                try {
                    B_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
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
            startDatabaseListener()
        }
    }
    override fun stopDatabaseListener() {
        listener?.let {
            B_ClientsDataBaseRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
