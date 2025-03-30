package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.Extension.FirebaseUtilsA_ProduitModelNewProto
import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class A_ProduitModelRepositoryImpl(
) : A_ProduitModelRepository {
    override var modelDatas: SnapshotStateList<A_ProduitModel> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    val connectivityMonitor = ConnectivityMonitorNewProto(CoroutineScope(Dispatchers.Default))

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsA_ProduitModelNewProto.initializeFirebaseOfflineCapability()
   //     startDatabaseListener()
        progressRepo.value = 1.0f

    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsA_ProduitModelNewProto.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    override fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        connectivityMonitor.checkConnectivityAndSync(
            A_ProduitModelRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            },
        )
    }

    override fun checkConnectivity() {
        connectivityMonitor.checkConnectivityAndSync(
            A_ProduitModelRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            }
        )
    }

    override fun updateData(data: A_ProduitModel?) {
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

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>> {
        checkConnectivityAndSync()
        return Pair(modelDatas.toList(), progressRepo)
    }

    private fun firebaseUpdateData(data: A_ProduitModel) {
        try {
            // Get reference to the specific product
            A_ProduitModelRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            // Optional: Handle unexpected error
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<A_ProduitModel>) {
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
                    A_ProduitModelRepository.caReference.child(data.id.toString()).setValue(data)
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
            A_ProduitModelRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
