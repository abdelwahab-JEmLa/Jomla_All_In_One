package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseInitFactory_8BonVent(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.GBonVentDao()
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    val repoRef = M8BonVent.ref
    val repoEntityName = "DataBaseInitFactory_8BonVent"
    val repoTAG = repoEntityName
    val name = Repository.Entity_8BonVent.name
    var isListenerRegistered = false

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        val isTableEmpty = dao.isTableEmpty()

        // Get items from Firebase that are either not available locally or have different timestamps
        val datas_With_Diffrent_Time_or_Non_Dispo_Au_Locale = if (isInternetAvailable && !isTableEmpty) {
            try {
                updateRepoProgress(name, 0.2f)

                // Get all local data
                val localData = dao.getAll()
                val localDataMap = localData.associateBy { it.keyID }

                updateRepoProgress(name, 0.4f)

                // Get Firebase data
                val firebaseData = onLoadFromFireBase()

                updateRepoProgress(name, 0.6f)

                // Filter Firebase items that need to be updated/added locally
                val itemsToSync = firebaseData.filter { fireBase_Data ->
                    val local_Data = localDataMap[fireBase_Data.keyID]

                    // Include if:
                    // 1. Item doesn't exist locally
                    // 2. Firebase item has newer timestamp than local item
                    local_Data == null ||
                            fireBase_Data.dernierTimeTampsSynchronisationAvecFireBase >= local_Data.dernierTimeTampsSynchronisationAvecFireBase
                }

                Log.d(repoTAG, "Found ${itemsToSync.size} items to sync from Firebase")
                itemsToSync

            } catch (e: Exception) {
                Log.e(repoTAG, "Error getting Firebase data for sync: ${e.message}", e)
                emptyList()
            }
        } else {
            emptyList()
        }

        // If table is empty, load initial data
        if (isTableEmpty) {
            updateRepoProgress(name, 0.4f)
            val data: List<M8BonVent> = if (isInternetAvailable) {
                updateRepoProgress(name, 0.6f)
                onLoadFromFireBase()
            } else {
                onLoadCategoriesFromCsv()
            }
            updateRepoProgress(name, 0.8f)
            dao.insertAll(data)
        }
        // If we have items to sync, update them
        else if (datas_With_Diffrent_Time_or_Non_Dispo_Au_Locale.isNotEmpty()) {
            updateRepoProgress(name, 0.8f)

            // Use upsert to insert new items or update existing ones
            datas_With_Diffrent_Time_or_Non_Dispo_Au_Locale.forEach { item ->
                dao.upsert(item)
            }

            Log.d(repoTAG, "Synced ${datas_With_Diffrent_Time_or_Non_Dispo_Au_Locale.size} items from Firebase")
        }

        updateRepoProgress(name, 1.0f)
    }

    suspend fun onLoadFromFireBase(): MutableList<M8BonVent> {
        return suspendCancellableCoroutine { continuation ->
            repoRef.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M8BonVent>()
                    snapshot.children.forEach { child ->
                        child.getValue(M8BonVent::class.java)?.let { item ->
                            dataList.add(item)
                        }
                    }
                    continuation.resume(dataList)
                }
                .addOnFailureListener {
                    throw IllegalStateException("No data available from Firebase or CSV")
                }
        }
    }

    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        Log.d(repoTAG, "Starting Firebase listener registration")

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(repoTAG, "Firebase data changed - processing ${snapshot.childrenCount} items")

                factoryScope.launch {
                    try {
                        val localData = dao.getAll()
                        val localDataMap = localData.associateBy { it.keyID }
                        val firebaseKeyIds = mutableSetOf<String>()

                        var updateCount = 0
                        var addCount = 0

                        Log.d(repoTAG, "Local entities count: ${localData.size}")

                        // Process Firebase data
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M8BonVent::class.java)?.let { fbEntity ->
                                    val entityWithKey = fbEntity.copy(keyID = child.key ?: "")
                                    firebaseKeyIds.add(entityWithKey.keyID)

                                    val localEntity = localDataMap[entityWithKey.keyID]

                                    when {
                                        localEntity == null -> {
                                            dao.upsert(entityWithKey)
                                            addCount++
                                            Log.d(
                                                repoTAG,
                                                "Added new entity: ${entityWithKey.keyID} ${
                                                    getEtate(entityWithKey)
                                                } " +
                                                        "with timestamp: ${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase}"
                                            )
                                        }

                                        else -> {
                                            dao.deleteByKeyId(entityWithKey.keyID)
                                            dao.insert(entityWithKey)
                                            updateCount++
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    repoTAG,
                                    "Error processing child ${child.key}: ${e.message}",
                                    e
                                )
                            }
                        }

                        val itemsToDelete = localDataMap.keys - firebaseKeyIds
                        var deleteCount = 0

                        for (keyToDelete in itemsToDelete) {
                            try {
                                dao.deleteByKeyId(keyToDelete)
                                deleteCount++
                                Log.d(repoTAG, "Deleted entity: $keyToDelete")
                            } catch (e: Exception) {
                                Log.e(
                                    repoTAG,
                                    "Error deleting entity $keyToDelete: ${e.message}",
                                    e
                                )
                            }
                        }

                        Log.i(
                            repoTAG,
                            "Sync complete - Added: $addCount, Updated: $updateCount, Deleted: $deleteCount"
                        )

                    } catch (e: Exception) {
                        Log.e(repoTAG, "Error in Firebase listener: ${e.message}", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
                Log.e(repoTAG, "Firebase listener cancelled: ${error.message}")
            }
        })
    }

    private fun getEtate(data: M8BonVent) =
        "etate est devenue == ${data.etateActuellementEst}"


    fun set(
        dataAvecTigerUpdate: M8BonVent,
    ) {
        factoryScope.launch {
            val entityWithUpdatedTimestamp = dataAvecTigerUpdate.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )

            dao.upsert(entityWithUpdatedTimestamp)
            batchFireBaseUpdateGBonVent(listOf(entityWithUpdatedTimestamp))
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M8BonVent>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }

    fun delete(data: M8BonVent) {
        factoryScope.launch {
            try {
                Log.d(repoTAG, "Deleting entity: ${data.keyID}")

                // Delete from local database first
                dao.delete(data)
                Log.d(repoTAG, "Local delete completed for: ${data.keyID}")

                // Then delete from Firebase
                repoRef.child(data.keyID).removeValue().await()
                Log.d(repoTAG, "Successfully deleted entity from Firebase: ${data.keyID}")
            } catch (e: Exception) {
                Log.e(repoTAG, "Error deleting entity ${data.keyID}: ${e.message}", e)
            }
        }
    }
}
