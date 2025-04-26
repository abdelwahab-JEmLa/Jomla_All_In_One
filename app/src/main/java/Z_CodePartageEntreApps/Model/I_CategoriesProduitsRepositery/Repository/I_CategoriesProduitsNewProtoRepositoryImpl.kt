package Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository

import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.Extension.FirebaseUtilsI_CategoriesProduitsNewProto
import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class I_CategoriesProduitsNewProtoRepositoryImpl(
) : I_CategoriesProduitsNewProtoRepository {
    override var modelDatas: SnapshotStateList<I_CategoriesProduits> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    val connectivityMonitor = ConnectivityMonitorNewProto(CoroutineScope(Dispatchers.Default))

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        startDatabaseListener {
        }
    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsI_CategoriesProduitsNewProto.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }


    override fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        connectivityMonitor.checkConnectivityAndSync(
            I_CategoriesProduitsNewProtoRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            },
        )

    }
    override fun checkConnectivity() {
        connectivityMonitor.checkConnectivityAndSync(
            I_CategoriesProduitsNewProtoRepository.caReference,
            onOnline = {
                restartDatabaseListener()
            }
        )

    }

    private fun dataFlomOffline(): () -> Unit = {
        I_CategoriesProduitsNewProtoRepository.caReference.get()
            .addOnSuccessListener { snapshot ->
                if (!this.isUpdating) {
                    try {
                        this.isUpdating = true
                        this.modelDatas.clear()

                        snapshot.children.forEach { dataSnapshot ->
                            try {
                                val data =
                                    I_CategoriesProduits.syncData(dataSnapshot = dataSnapshot) as I_CategoriesProduits
                                if (data.id > 0) {
                                    this.modelDatas.add(data)
                                }
                            } catch (e: Exception) {
                            }
                        }

                        this.progressRepo.value = 1.0f
                        this.initialDataLoaded = true
                    } finally {
                        this.isUpdating = false
                    }
                }
            }
            .addOnFailureListener {}
    }

    override fun updateData(data: I_CategoriesProduits?) {
        if (data == null) return

        // Find the index of the record in the modelDatas list
        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            // Update the record in the modelDatas list
            modelDatas[recordIndex] = data

            try {
                // Check connectivity before trying to upsert_1_3_TransactionCommercial Firebase
                checkConnectivity()

                // Update Firebase database with the updated record
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Silent catch
            }
        }
    }

    private fun firebaseUpdateData(data: I_CategoriesProduits) {
        try {
            val firebaseData = I_CategoriesProduits.syncData() as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey =
                FirebaseUtilsI_CategoriesProduitsNewProto.sanitizeFirebaseKey(data.id.toString())

            // Update the data in Firebase
            I_CategoriesProduitsNewProtoRepository.caReference.child(sanitizedKey)
                .updateChildren(firebaseData)
        } catch (e: Exception) {
            // Silent catch
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<I_CategoriesProduits>, Flow<Float>> {
        // Check connectivity before loading
        checkConnectivityAndSync()

        // Return the current list of model data and the progress repository flow
        return Pair(modelDatas.toList(), progressRepo)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<I_CategoriesProduits>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to upsert_1_3_TransactionCommercial
            checkConnectivity()

            datas.forEach { data ->
                try {
                    val firebaseData =
                        I_CategoriesProduits.syncData(data = data) as Map<String, Any>

                    // Sanitize the key before using it in Firebase
                    val sanitizedKey =
                        FirebaseUtilsI_CategoriesProduitsNewProto.sanitizeFirebaseKey(data.id.toString())

                    // Update the data in Firebase
                    I_CategoriesProduitsNewProtoRepository.caReference.child(sanitizedKey)
                        .updateChildren(firebaseData)

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
            I_CategoriesProduitsNewProtoRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
