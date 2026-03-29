package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataBaseInit_Z_AppCompt(
    val dao: Dao_M9AppCompt,
) {
    val repoEntityName ="Z_AppComptRepositoryProtoJuin17"
    val repoTAG = repoEntityName
    var isListenerRegistered = false

    val repoRef = M09AppCompt.ref


    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.4f)

        val data: List<M09AppCompt> = if (isInternetAvailable) {

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
        M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M09AppCompt::class.java)?.let { entity ->
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
        }) }
    }


    fun addOrUpdatedDataBase(
        existingIndex: Int,
        dataAvecTigerUpdate: M09AppCompt
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

    private suspend fun batchFireBaseUpdateZ_AppCompt(datas: List<M09AppCompt>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }
}
