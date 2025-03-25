package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.Extension.FirebaseUtilsA_ProduitModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class A_ProduitModelNewProtoRepositoryImpl(
) : A_ProduitModelNewProtoRepository {
    override var modelDatas: SnapshotStateList<A_ProduitModel> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsA_ProduitModel.initializeFirebaseOfflineCapability()
        startDatabaseListener()

    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsA_ProduitModel.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        FirebaseUtilsA_ProduitModel.checkConnectivityAndSync(this)
    }

    override fun updateData(data: A_ProduitModel?) {
        if (data == null) return

        // Find the index of the record in the modelDatas list
        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            // Update the record in the modelDatas list
            modelDatas[recordIndex] = data

            try {
                // Check connectivity before trying to update Firebase
                checkConnectivityAndSync()

                // Update Firebase database with the updated record
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Silent catch
            }
        }
    }

    private fun firebaseUpdateData(data: A_ProduitModel) {
        try {
            val firebaseData = A_ProduitModel.syncData() as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = FirebaseUtilsA_ProduitModel.sanitizeFirebaseKey(data.id.toString())

            // Update the data in Firebase
            A_ProduitModelNewProtoRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)
        } catch (e: Exception) {
            // Silent catch
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>> {
        return FirebaseUtilsA_ProduitModel.onDataBaseChangeListnerAndLoad(this)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<A_ProduitModel>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to update
            checkConnectivityAndSync()

            datas.forEach { data ->
                try {
                    val firebaseData = A_ProduitModel.syncData(data = data) as Map<String, Any>

                    // Sanitize the key before using it in Firebase
                    val sanitizedKey = FirebaseUtilsA_ProduitModel.sanitizeFirebaseKey(data.id.toString())

                    // Update the data in Firebase
                    A_ProduitModelNewProtoRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)

                    processedItems++
                    progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                } catch (e: Exception) {
                    // Silent catch
                }
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
        } finally {
            isUpdating = false
            startDatabaseListener() // Restart the database listener
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            A_ProduitModelNewProtoRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
