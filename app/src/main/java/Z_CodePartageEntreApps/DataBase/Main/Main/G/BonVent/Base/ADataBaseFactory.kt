package Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataBaseCreationFactoryGBonVent(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.GBonVentDao()
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    val repoRef = M8BonVent.ref
    val repoEntityName = "DataBaseCreationFactoryGBonVent"
    val repoTAG = repoEntityName
    val name = Repository.GBonVentEntity.name
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

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M8BonVent::class.java)?.let { entity ->
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
        dataAvecTigerUpdate: M8BonVent,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
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

    }
}
