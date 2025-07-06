package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseCreationFactory13TarificationInfos(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.Dao13TarificationInfos()
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    val repoRef = M13TarificationInfos.ref
    val repoEntityName = "DataBaseCreationFactoryM13TarificationInfos"
    val repoTAG = repoEntityName
    val name = Repository.M13TarificationInfosEntity.name
    var isListenerRegistered = false


    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(name, 0.4f)

        val data: List<M13TarificationInfos> = if (isInternetAvailable) {

            updateRepoProgress(name, 0.6f)
            suspendCancellableCoroutine { continuation ->
                repoRef.get()
                    .addOnSuccessListener { snapshot ->
                        val dataList = mutableListOf<M13TarificationInfos>()
                        snapshot.children.forEach { child ->
                            child.getValue(M13TarificationInfos::class.java)?.let { item ->
                                dataList.add(item)
                            }
                        }
                        continuation.resume(dataList)
                    }
                    .addOnFailureListener {
                        throw IllegalStateException("No data available from Firebase or CSV")
                    }
            }
        } else {
            TODO("")
        }

        updateRepoProgress(name, 0.8f)


        dao.insertAll(data)
    }

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
                                child.getValue(M13TarificationInfos::class.java)?.let { entity ->
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
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
            }
        })
    }

    fun set(
        dataAvecTigerUpdate: M13TarificationInfos,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdateM13TarificationInfos(listOf(dataAvecTigerUpdate))
        }
    }

    private suspend fun batchFireBaseUpdateM13TarificationInfos(datas: List<M13TarificationInfos>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data.copy(
                keyID = data.keyID
            )
        }
        repoRef.updateChildren(updates).await()
    }

    fun delete(data: M13TarificationInfos) {
        factoryScope.launch {
            try {
                dao.delete(data)
                // Also remove from Firebase
                repoRef.child(data.keyID).removeValue().await()
            } catch (e: Exception) {
                // Handle deletion error
            }
        }
    }
}
