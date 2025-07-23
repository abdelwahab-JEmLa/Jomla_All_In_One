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
        Log.d(repoTAG, "init() - Starting initialization, isInternetAvailable: $isInternetAvailable")

        if (!dao.isTableEmpty()) {
            Log.d(repoTAG, "init() - Table not empty, skipping initialization")
            return
        }

        Log.d(repoTAG, "init() - Table is empty, starting data load")
        updateRepoProgress(name, 0.4f)

        val data: List<M8BonVent> = if (isInternetAvailable) {
            Log.d(repoTAG, "init() - Loading from Firebase")
            updateRepoProgress(name, 0.6f)
            onLoadFromFireBase()
        } else {
            Log.d(repoTAG, "init() - Loading from CSV (offline mode)")
            onLoadCategoriesFromCsv()
        }

        Log.d(repoTAG, "init() - Loaded ${data.size} items")
        updateRepoProgress(name, 0.8f)

        dao.insertAll(data)
        Log.d(repoTAG, "init() - Initialization completed successfully")
    }

    fun triggerUpdateFbParTimestampsListener() {
        Log.d(repoTAG, "triggerUpdateFbParTimestampsListener() - Attempting to register listener")

        if (isListenerRegistered) {
            Log.d(repoTAG, "triggerUpdateFbParTimestampsListener() - Listener already registered, skipping")
            return
        }

        isListenerRegistered = true
        Log.d(repoTAG, "triggerUpdateFbParTimestampsListener() - Listener registered successfully")

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(repoTAG, "onDataChange() - Firebase data changed, processing ${snapshot.childrenCount} items")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Get all current local entities
                        val localEntities = dao.getAll()
                        val localEntityKeys = localEntities.map { it.keyID }.toSet()
                        val firebaseEntityKeys = mutableSetOf<String>()

                        var addCount = 0
                        var updateCount = 0
                        var deleteCount = 0

                        Log.d(repoTAG, "onDataChange() - Local entities: ${localEntities.size}, Firebase entities: ${snapshot.childrenCount}")

                        // Handle updates and additions
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M8BonVent::class.java)?.let { entity ->
                                    val entityWithKey = entity.copy(keyID = child.key ?: "")
                                    firebaseEntityKeys.add(entityWithKey.keyID)

                                    val localEntity = localEntities.find { it.keyID == entityWithKey.keyID }

                                    if (localEntity == null) {
                                        // ADD operation
                                        dao.insert(entityWithKey)
                                        addCount++
                                        Log.d(repoTAG, "onDataChange() - ADD: ${entityWithKey.keyID}")
                                    } else {
                                        // UPDATE operation - check timestamp
                                        if (entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase) {
                                            dao.update(entityWithKey)
                                            updateCount++
                                            Log.d(repoTAG, "onDataChange() - UPDATE: ${entityWithKey.keyID} (FB: ${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase}, Local: ${localEntity.dernierTimeTampsSynchronisationAvecFireBase})")
                                        } else {
                                            Log.v(repoTAG, "onDataChange() - SKIP: ${entityWithKey.keyID} - local timestamp is newer or equal")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(repoTAG, "onDataChange() - Error processing child: ${child.key}", e)
                            }
                        }

                        // Handle deletions
                        val entitiesToDelete = localEntityKeys - firebaseEntityKeys
                        for (keyToDelete in entitiesToDelete) {
                            localEntities.find { it.keyID == keyToDelete }?.let { entityToDelete ->
                                dao.delete(entityToDelete)
                                deleteCount++
                                Log.d(repoTAG, "onDataChange() - DELETE: ${entityToDelete.keyID}")
                            }
                        }

                        Log.i(repoTAG, "onDataChange() - Synchronization completed: ADD=$addCount, UPDATE=$updateCount, DELETE=$deleteCount")

                    } catch (e: Exception) {
                        Log.e(repoTAG, "onDataChange() - Error during synchronization", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
                Log.e(repoTAG, "onCancelled() - Firebase listener cancelled: ${error.message}", error.toException())
            }
        })
    }

    fun set(
        dataAvecTigerUpdate: M8BonVent,
    ) {
        Log.d(repoTAG, "set() - Updating entity: ${dataAvecTigerUpdate.keyID}")

        factoryScope.launch {
            try {
                // Update timestamp before Firebase sync to ensure proper synchronization
                val currentTimestamp = System.currentTimeMillis()
                val dataWithUpdatedTimestamp = dataAvecTigerUpdate.copy(
                    dernierTimeTampsSynchronisationAvecFireBase = currentTimestamp
                )

                // First update locally
                dao.upsert(dataWithUpdatedTimestamp)
                Log.d(repoTAG, "set() - Local upsert completed for: ${dataWithUpdatedTimestamp.keyID}")

                // Then update Firebase with the new timestamp
                batchFireBaseUpdateGBonVent(listOf(dataWithUpdatedTimestamp))
                Log.d(repoTAG, "set() - Firebase update completed for: ${dataWithUpdatedTimestamp.keyID}")
            } catch (e: Exception) {
                Log.e(repoTAG, "set() - Error updating entity: ${dataAvecTigerUpdate.keyID}", e)
            }
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M8BonVent>) {
        Log.d(repoTAG, "batchFireBaseUpdateGBonVent() - Updating ${datas.size} entities to Firebase")

        try {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.keyID] = data
                Log.v(repoTAG, "batchFireBaseUpdateGBonVent() - Preparing update for: ${data.keyID}")
            }

            repoRef.updateChildren(updates).await()
            Log.d(repoTAG, "batchFireBaseUpdateGBonVent() - Firebase batch update completed successfully")
        } catch (e: Exception) {
            Log.e(repoTAG, "batchFireBaseUpdateGBonVent() - Error during Firebase batch update", e)
            throw e
        }
    }

    fun delete(data: M8BonVent) {
        Log.d(repoTAG, "delete() - Deleting entity: ${data.keyID}")

        factoryScope.launch {
            try {
                dao.delete(data)
                Log.d(repoTAG, "delete() - Local deletion completed for: ${data.keyID}")

                repoRef.child(data.keyID).removeValue().await()
                Log.d(repoTAG, "delete() - Firebase deletion completed for: ${data.keyID}")
            } catch (e: Exception) {
                Log.e(repoTAG, "delete() - Error deleting entity: ${data.keyID}", e)
            }
        }
    }
}
