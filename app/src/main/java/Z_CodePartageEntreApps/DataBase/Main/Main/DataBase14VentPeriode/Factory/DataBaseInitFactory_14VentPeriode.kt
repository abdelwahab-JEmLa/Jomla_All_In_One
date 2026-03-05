package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory

import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Modules.AppDatabase
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

class DataBaseInitFactory_14VentPeriode(
    val appDatabase: AppDatabase
) {
    val dao =appDatabase.dao_M14VentPeriode()
    val repoTAG = "M14VentPeriode_Entity"
    val repoRef = M14VentPeriode.ref
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(WDatabaseInitializationManager.Repository.M3CouleurProduitInfos_Entit.name, 0.4f)
        val data: List<M14VentPeriode> = if (isInternetAvailable) {
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
    private fun onLoadFromCsv(): List<M14VentPeriode> = emptyList()
    suspend fun onLoadFromFireBase(): MutableList<M14VentPeriode> {
        return suspendCancellableCoroutine { continuation ->
            M14VentPeriode.ref.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M14VentPeriode>()
                    snapshot.children.forEach { child ->
                        child.getValue(M14VentPeriode::class.java)?.let { item ->
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
                                child.getValue(M14VentPeriode::class.java)
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
    fun upsert(
        dataAvecTigerUpdate: M14VentPeriode,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M14VentPeriode>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }

    fun delete(data: M14VentPeriode) {
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
