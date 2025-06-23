package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager.Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Z_AppComptDao
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Z_AppComptRepositoryProtoJuin17(
    val dao: Z_AppComptDao,
) {
    val repoEntityName ="Z_AppComptRepositoryProtoJuin17"
    val repoTAG = repoEntityName
    var isListenerRegistered = false

    val repoRef = Z_AppCompt.caRef


    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.4f)

        val data: List<Z_AppCompt> = if (isInternetAvailable) {

            updateRepoProgress(Repository.Z_AppComptEntity.name, 0.6f)

            onLoadFromFireBase()
        } else {
            onLoadCategoriesFromCsv()
        }

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.8f)

        Log.d(
            repoTAG,
            "${data.map { it.nom }}"
        )

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
                                child.getValue(Z_AppCompt::class.java)?.let { entity ->
                                    val entityWithKey = entity.copy(bsonObjectId = child.key ?: "")
                                    val shouldUpdate = try {
                                        val localEntity = dao.getAll().find { it.bsonObjectId == entityWithKey.bsonObjectId }
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
                            } catch (e: Exception) {}
                        }
                    } catch (e: Exception) {}
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
            }
        })
    }


    fun addOrUpdatedDataBase(
        existingIndex: Int,
        dataAvecTigerUpdate: Z_AppCompt
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateZ_AppCompt(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateZ_AppCompt(listOf(dataAvecTigerUpdate))
            }
        }
    }


    private suspend fun batchFireBaseUpdateZ_AppCompt(datas: List<Z_AppCompt>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.bsonObjectId] = data
        }
        repoRef.updateChildren(updates).await()
    }
}
