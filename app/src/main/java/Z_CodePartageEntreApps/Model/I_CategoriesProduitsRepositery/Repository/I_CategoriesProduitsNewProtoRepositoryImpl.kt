package Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository

import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.Extension.FirebaseUtilsI_CategoriesProduits
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class I_CategoriesProduitsNewProtoRepositoryImpl(
) : I_CategoriesProduitsNewProtoRepository {
    override var modelDatas: SnapshotStateList<I_CategoriesProduits> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {
        FirebaseUtilsI_CategoriesProduits.initializeFirebaseOfflineCapability()
        startDatabaseListener()

    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsI_CategoriesProduits.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        FirebaseUtilsI_CategoriesProduits.checkConnectivityAndSync(this)
    }

    override fun updateData(data: I_CategoriesProduits?) {
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

    private fun firebaseUpdateData(data: I_CategoriesProduits) {
        try {
            val firebaseData = I_CategoriesProduits.syncData() as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = FirebaseUtilsI_CategoriesProduits.sanitizeFirebaseKey(data.id.toString())

            // Update the data in Firebase
            I_CategoriesProduitsNewProtoRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)
        } catch (e: Exception) {
            // Silent catch
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<I_CategoriesProduits>, Flow<Float>> {
        return FirebaseUtilsI_CategoriesProduits.onDataBaseChangeListnerAndLoad(this)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<I_CategoriesProduits>) {
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
                    val firebaseData = I_CategoriesProduits.syncData(data = data) as Map<String, Any>

                    // Sanitize the key before using it in Firebase
                    val sanitizedKey = FirebaseUtilsI_CategoriesProduits.sanitizeFirebaseKey(data.id.toString())

                    // Update the data in Firebase
                    I_CategoriesProduitsNewProtoRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)

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
