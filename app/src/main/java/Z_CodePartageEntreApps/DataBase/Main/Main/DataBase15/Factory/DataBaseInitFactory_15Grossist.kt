package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory

import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseInitFactory_15Grossist(
    val appDatabase: AppDatabase
) {
    val dao =appDatabase.Dao15Grossist()
    val repoTAG = "M15Grossist_Entity"
    val repoRef = M15Grossist.ref
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.4f)
        val data: List<M15Grossist> = if (isInternetAvailable) {
            updateRepoProgress(
                WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name,
                0.6f
            )
            onLoadFromFireBase()
        } else {
            onLoadFromCsv()
        }
        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
    }
    private fun onLoadFromCsv(): List<M15Grossist> = emptyList()
    suspend fun onLoadFromFireBase(): MutableList<M15Grossist> {
        return suspendCancellableCoroutine { continuation ->
            M15Grossist.ref.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M15Grossist>()
                    snapshot.children.forEach { child ->
                        child.getValue(M15Grossist::class.java)?.let { item ->
                            dataList.add(item)
                        }
                    }
                    continuation.resume(dataList)
                }
                .addOnFailureListener { exception ->
                    println("Firebase load error: ${exception.message}")
                    continuation.resume(mutableListOf())
                }
        }
    }

    var isListenerRegistered = false
    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M15Grossist::class.java)
                                    ?.let { entity ->
                                        val entityWithKey = entity.copy(keyID = child.key ?: "")
                                        val shouldUpdate = try {
                                            val localEntity =
                                                dao.getAll().find { it.keyID == entityWithKey.keyID }
                                            if (localEntity == null) {
                                                true
                                            } else {
                                                entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase
                                            }
                                        } catch (e: Exception) {
                                            true
                                        }

                                        if (shouldUpdate) {
                                            dao.update(entityWithKey)
                                            updateCount++
                                        }
                                    }
                            } catch (e: Exception) {
                                println("Error processing child: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Error in data change listener: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase listener cancelled: ${error.message}")
                isListenerRegistered = false
            }
        })
    }

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: M15Grossist
    ) {
        factoryScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
            }
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M15Grossist>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }

    fun delete(data: M15Grossist) {
        factoryScope.launch {
            try {
                dao.delete(data)
                repoRef.child(data.keyID).removeValue().await()
            } catch (e: Exception) {
                println("Error in deleteDataAncienRepo: ${e.message}")
            }
        }
    }

    fun deleteAll() {
        factoryScope.launch {
            try {
                dao.deleteAll()
                repoRef.removeValue().await()
            } catch (e: Exception) {
                println("Error deleting all data: ${e.message}")
            }
        }
    }
}
