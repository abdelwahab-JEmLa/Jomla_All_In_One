package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
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

class DataBaseInitFactory_16CategorieProduit(
    val appDatabase: AppDatabase
) {
    val dao =appDatabase.Dao16CategorieProduit()
    val repoTAG = "M16CategorieProduit_Entity"
    val repoRef = CategoriesTabelle.ref
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(WDatabaseInitializationManager.Repository.M3CouleurProduitInfos_Entit.name, 0.4f)
        val data: List<CategoriesTabelle> = if (isInternetAvailable) {
            updateRepoProgress(
                WDatabaseInitializationManager.Repository.M3CouleurProduitInfos_Entit.name,
                0.6f
            )
            onLoadFromFireBase()
        } else {
            onLoadFromCsv()
        }
        updateRepoProgress(WDatabaseInitializationManager.Repository.M3CouleurProduitInfos_Entit.name, 0.8f)
        dao.insertAll(data)
    }
    private fun onLoadFromCsv(): List<CategoriesTabelle> = emptyList()
    suspend fun onLoadFromFireBase(): MutableList<CategoriesTabelle> {
        return suspendCancellableCoroutine { continuation ->
            CategoriesTabelle.ref.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<CategoriesTabelle>()
                    snapshot.children.forEach { child ->
                        child.getValue(CategoriesTabelle::class.java)?.let { item ->
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

// Update the Factory class methods to use transactions properly:

    // In DataBaseInitFactory_16CategorieProduit class, update the addOrUpdatedAncienRepo method:
    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: CategoriesTabelle,
        avec_BatchFireBase: Boolean = true
    ) {
        factoryScope.launch {
            try {
                dao.transaction {
                    if (existingIndex >= 0) {
                        dao.update(dataAvecTigerUpdate)
                    } else {
                        dao.insert(dataAvecTigerUpdate)
                    }
                }

                if (avec_BatchFireBase) {
                    batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
                }
            } catch (e: Exception) {
                Log.e(repoTAG, "Error in addOrUpdatedAncienRepo: ${e.message}")
            }
        }
    }

    // Add a new method for bulk operations with transaction:
    suspend fun bulkReplaceAll(newData: List<CategoriesTabelle>) {
        try {
            dao.transaction {
                deleteAll()
                insertAll(newData)
            }
            Log.d(repoTAG, "Bulk replace completed for ${newData.size} items")
        } catch (e: Exception) {
            Log.e(repoTAG, "Error in bulk replace: ${e.message}")
            throw e
        }
    }

    // Enhanced delete method with transaction:
    fun delete(data: CategoriesTabelle) {
        factoryScope.launch {
            try {
                dao.transaction {
                    delete(data)
                }
                repoRef.child(data.keyID).removeValue().await()
            } catch (e: Exception) {
                Log.e(repoTAG, "Error in delete: ${e.message}")
            }
        }
    }

    // Enhanced deleteAll method with transaction:
    fun deleteAll() {
        factoryScope.launch {
            try {
                dao.transaction {
                    deleteAll()
                }
                repoRef.removeValue().await()
            } catch (e: Exception) {
                Log.e(repoTAG, "Error deleting all data: ${e.message}")
            }
        }
    }

    var isListenerRegistered = false
    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true
        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(CategoriesTabelle::class.java)
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
        })}
    }


    private suspend fun batchFireBaseUpdateGBonVent(datas: List<CategoriesTabelle>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }


}
