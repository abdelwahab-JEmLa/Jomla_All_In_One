package Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository

import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepository
import Z_CodePartageEntreApps.Model.P_BonsCommandGrossist
import Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository.Extension.FirebaseUtilsP_BonsCommandGrossist
import Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository.Extension.OperationHandler_Model_P_BonsCommandGrossist
import Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository.Extension.SyncDataUtilsP_BonsCommandGrossist
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class P_BonsCommandGrossistRepositoryImpl(
    val soldArticlesTabelleRepository: SoldArticlesTabelleRepository
) :P_BonsCommandGrossistRepository {
    override var modelDatas: SnapshotStateList<P_BonsCommandGrossist> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false

    init {

        // Monitor the progress of soldArticlesTabelleRepository
        CoroutineScope(Dispatchers.Main).launch {
            soldArticlesTabelleRepository.progressRepo.collectLatest { progress ->
                // Update our progress based on the SoldArticlesTabelle repository progress
                if (!isUpdating) {
                    progressRepo.value = progress * 0.5f  // Weight SoldArticlesTabelle progress as 50% of our progress
                }

                // When SoldArticlesTabelle is loaded, start our database listener
                if (progress >= 1.0f) {
                    startDatabaseListener()
                }
            }
        }
    }

    private fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        initialDataLoaded = false

        FirebaseUtilsP_BonsCommandGrossist.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    fun checkAndInitializeIfNeeded(
        soldArticlesTabelleRepository: SoldArticlesTabelleRepository,
    ) {
        // Don't proceed until the ValueEventListener has completed initial data loading
        if (!initialDataLoaded) {
            return
        }

        // Calculate what the data should be based on current SoldArticlesTabelle
        val dataCalcule = OperationHandler_Model_P_BonsCommandGrossist
            .groupedProduitsParGrossist(soldArticlesTabelleRepository.modelDatas)

        // Get total product IDs from calculated data
        val calculatedProductIdsCount = dataCalcule.sumOf { it.produitCommendeIDs.size }

        // Get total product IDs from current modelDatas
        val currentProductIdsCount = modelDatas.sumOf { it.produitCommendeIDs.size }

        // Check if we need to upsertLenceCommandeRepoGroupedProtoAvantJuin3 based on product ID count differences
        if (currentProductIdsCount != calculatedProductIdsCount || modelDatas.isEmpty()) {
            // Clear Firebase data
            P_BonsCommandGrossistRepository.caReference.removeValue()

            // Update local data
            modelDatas.clear()
            modelDatas.addAll(dataCalcule)

            try {
                // Save directly to Firebase
                dataCalcule.forEach { data ->
                    try {
                        val firebaseData = SyncDataUtilsP_BonsCommandGrossist.syncData(data = data) as Map<String, Any>
                        val sanitizedKey = FirebaseUtilsP_BonsCommandGrossist.sanitizeFirebaseKey(data.vid.toString())
                        P_BonsCommandGrossistRepository.caReference.child(sanitizedKey).setValue(firebaseData)
                    } catch (e: Exception) {
                        // Silent catch
                    }
                }
            } catch (e: Exception) {
                // Silent catch
            }
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        FirebaseUtilsP_BonsCommandGrossist.checkConnectivityAndSync(this)
    }

    override fun updateData(data: P_BonsCommandGrossist?) {
        if (data == null) return

        // Find the index of the record in the modelDatas list
        val recordIndex = modelDatas.indexOfFirst { it.vid == data.vid }

        if (recordIndex != -1) {
            // Update the record in the modelDatas list
            modelDatas[recordIndex] = data

            try {
                // Check connectivity before trying to upsertLenceCommandeRepoGroupedProtoAvantJuin3 Firebase
                checkConnectivityAndSync()

                // Update Firebase database with the updated record
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Silent catch
            }
        }
    }

    private fun firebaseUpdateData(data: P_BonsCommandGrossist) {
        try {
            val firebaseData = SyncDataUtilsP_BonsCommandGrossist.syncData(tempTravaille = data) as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = FirebaseUtilsP_BonsCommandGrossist.sanitizeFirebaseKey(data.vid.toString())

            // Update the data in Firebase
            P_BonsCommandGrossistRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)
        } catch (e: Exception) {
            // Silent catch
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<P_BonsCommandGrossist>, Flow<Float>> {
        return FirebaseUtilsP_BonsCommandGrossist.onDataBaseChangeListnerAndLoad(this)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<P_BonsCommandGrossist>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to upsertLenceCommandeRepoGroupedProtoAvantJuin3
            checkConnectivityAndSync()

            datas.forEach { data ->
                try {
                    val firebaseData = SyncDataUtilsP_BonsCommandGrossist.syncData(data = data) as Map<String, Any>

                    // Sanitize the key before using it in Firebase
                    val sanitizedKey = FirebaseUtilsP_BonsCommandGrossist.sanitizeFirebaseKey(data.vid.toString())

                    // Update the data in Firebase
                    P_BonsCommandGrossistRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)

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
            P_BonsCommandGrossistRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
