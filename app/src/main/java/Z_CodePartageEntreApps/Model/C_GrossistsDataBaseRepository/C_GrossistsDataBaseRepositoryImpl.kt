package Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository

import Z_CodePartageEntreApps.Model.C_GrossistsDataBase
import Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository.Extension.FirebaseUtilsC_GrossistsDataBase
import Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository.Extension.SyncDataUtilsC_GrossistsDataBase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class C_GrossistsDataBaseRepositoryImpl : C_GrossistsDataBaseRepository {
    override var modelDatas: SnapshotStateList<C_GrossistsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L

    init {
        startDatabaseListener()
    }

    private fun startDatabaseListener() {
        stopDatabaseListener()
        FirebaseUtilsC_GrossistsDataBase.startDatabaseListener(this) { newListener ->
            listener = newListener
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        FirebaseUtilsC_GrossistsDataBase.checkConnectivityAndSync(this)
    }

    override fun updateData(data: C_GrossistsDataBase?) {
        if (data == null) return

        // Find the index of the record in the modelDatas list
        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            // Update the record in the modelDatas list
            modelDatas[recordIndex] = data

            try {
                // Check connectivity before trying to upsert_1_3_TransactionCommercial Firebase
                checkConnectivityAndSync()

                // Update Firebase database with the updated record
                firebaseUpdateData(data)
            } catch (e: Exception) {
                println("Firebase upsert_1_3_TransactionCommercial failed in updateUnSeulData: ${e.message}")
            }
        }
    }

    // Implementation for the new addData method
    override fun addData(data: C_GrossistsDataBase) {
        try {
            // Check if the data already exists in the list
            val existingIndex = modelDatas.indexOfFirst { it.id == data.id }

            if (existingIndex == -1) {
                // Add the data to the local list if it doesn't exist
                modelDatas.add(data)

                // Check connectivity before trying to upsert_1_3_TransactionCommercial Firebase
                checkConnectivityAndSync()

                // Add the data to Firebase
                val firebaseData = SyncDataUtilsC_GrossistsDataBase.syncData(tempTravaille = data) as Map<String, Any>

                // Sanitize the key before using it in Firebase
                val sanitizedKey = FirebaseUtilsC_GrossistsDataBase.sanitizeFirebaseKey(data.id.toString())

                // Set the data in Firebase
                C_GrossistsDataBaseRepository.caReference.child(sanitizedKey).setValue(firebaseData)
                    .addOnFailureListener { e -> println("Firebase add failed: ${e.message}") }
            } else {
                // If it already exists, upsert_1_3_TransactionCommercial it instead
                updateData(data)
            }
        } catch (e: Exception) {
            println("Failed to add data: ${e.message}")
        }
    }

    private fun firebaseUpdateData(data: C_GrossistsDataBase) {
        try {
            val firebaseData = SyncDataUtilsC_GrossistsDataBase.syncData(tempTravaille = data) as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = FirebaseUtilsC_GrossistsDataBase.sanitizeFirebaseKey(data.id.toString())

            // Update the data in Firebase
            C_GrossistsDataBaseRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)
                .addOnFailureListener { e -> println("Firebase upsert_1_3_TransactionCommercial failed: ${e.message}") }
        } catch (e: Exception) {
            println("Failed to prepare data: ${e.message}")
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<C_GrossistsDataBase>, Flow<Float>> {
        return FirebaseUtilsC_GrossistsDataBase.onDataBaseChangeListnerAndLoad(this)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<C_GrossistsDataBase>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to upsert_1_3_TransactionCommercial
            checkConnectivityAndSync()

            datas.forEach { data ->
                val firebaseData = SyncDataUtilsC_GrossistsDataBase.syncData(data = data) as Map<String, Any>

                // Sanitize the key before using it in Firebase
                val sanitizedKey = FirebaseUtilsC_GrossistsDataBase.sanitizeFirebaseKey(data.id.toString())

                // Update the data in Firebase
                C_GrossistsDataBaseRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)

                processedItems++
                progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
            println("Failed to upsert_1_3_TransactionCommercial data batch: ${e.message}")
        } finally {
            isUpdating = false
            startDatabaseListener() // Restart the database listener
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            C_GrossistsDataBaseRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
