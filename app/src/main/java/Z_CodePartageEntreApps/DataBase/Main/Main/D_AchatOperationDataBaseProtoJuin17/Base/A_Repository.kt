package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base

import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager.Repository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init.onLoadCategoriesFromCsvD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init.onLoadFromFireBaseD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.D_AchatOperationDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataBaseFactoryDCouleurAchatOperation(
    val dao: D_AchatOperationDao,
) {
    val repoTAG = "FCouleurVentOperationInfos"
    val repoRef = FCouleurVentOperationInfos.ref
    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.4f)
        val data: List<FCouleurVentOperationInfos> = if (isInternetAvailable) {
            updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.6f)
            onLoadFromFireBaseD_AchatOperation()
        } else {
            onLoadCategoriesFromCsvD_AchatOperation()
        }
        updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
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
                                child.getValue(FCouleurVentOperationInfos::class.java)?.let { entity ->
                                    val entityWithKey = entity.copy(keyID = child.key ?: "")
                                    val shouldUpdate = try {
                                        val localEntity = dao.getAll().find { it.keyID == entityWithKey.keyID }
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


    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: FCouleurVentOperationInfos
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateD_AchatOperation(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateD_AchatOperation(listOf(dataAvecTigerUpdate))
            }
        }
    }


    private suspend fun batchFireBaseUpdateD_AchatOperation(datas: List<FCouleurVentOperationInfos>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        val firebaseRef = FCouleurVentOperationInfos.ref
        firebaseRef.updateChildren(updates).await()
    }
}
