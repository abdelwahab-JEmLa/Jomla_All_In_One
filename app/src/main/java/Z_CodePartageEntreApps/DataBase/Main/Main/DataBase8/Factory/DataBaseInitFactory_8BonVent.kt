package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
        if (!dao.isTableEmpty()) return

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
                                            dao.upsert(entityWithKey)
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
