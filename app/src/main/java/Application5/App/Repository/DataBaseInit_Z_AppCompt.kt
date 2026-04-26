package Application5.App.Repository

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Modules.Loading_Datas.Init.WDatabaseInitializationManager.Repository
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
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

class DataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt(
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
            onLoadFromFireBase()
        }

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.8f)

        Log.d(
            repoTAG,
            "${data.map { it.nom }}"
        )

        dao.insertAll(data)
    }
    suspend fun onLoadFromFireBase(): MutableList<M09AppCompt> {
        return suspendCancellableCoroutine { continuation ->
            repoRef.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M09AppCompt>()
                    snapshot.children.forEach { child ->
                        child.getValue(M09AppCompt::class.java)?.let { item ->
                            dataList.add(item)
                        }
                    }
                    continuation.resume(dataList)
                }
                .addOnFailureListener {
                    throw IllegalStateException("No data available from Firebase or CSV")
                }
        }
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
