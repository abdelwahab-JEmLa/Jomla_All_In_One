package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Z_AppComptDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class Z_AppComptRepositoryProtoJuin17(
    val dao: Z_AppComptDao,
) {
    val repoEntityName ="Z_AppComptRepositoryProtoJuin17"
    val name = Repository.Z_AppComptEntity.name

    val repoTAG = repoEntityName
    var isListenerRegistered = false

    val repoRef = Z_AppCompt.ref


    private val factoryScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        val isTableEmpty = dao.isTableEmpty()

        val datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale = if (isInternetAvailable && !isTableEmpty) {
            try {
                updateRepoProgress(name, 0.2f)
                val localData = dao.getAll()
                val localDataMap = localData.associateBy { it.keyID }
                updateRepoProgress(name, 0.4f)
                val firebaseData = onLoadFromFireBase()
                updateRepoProgress(name, 0.6f)

                firebaseData.filter { fireBase_Data ->
                    val local_Data = localDataMap[fireBase_Data.keyID]
                    local_Data == null ||
                            fireBase_Data.dernierTimeTampsSynchronisationAvecFireBase >= local_Data.dernierTimeTampsSynchronisationAvecFireBase
                }
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        if (isTableEmpty) {
            updateRepoProgress(name, 0.4f)
            val data: List<Z_AppCompt> = if (isInternetAvailable) {
                updateRepoProgress(name, 0.6f)
                onLoadFromFireBase()
            } else {
                onLoadCategoriesFromCsv()
            }
            updateRepoProgress(name, 0.8f)
            dao.insertAll(data)
        } else if (datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale.isNotEmpty()) {
            updateRepoProgress(name, 0.8f)
            datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale.forEach { item ->
                dao.upsert(item)
            }
        }

        updateRepoProgress(name, 1.0f)
    }

    suspend fun onLoadFromFireBase(): MutableList<Z_AppCompt> {
        return suspendCancellableCoroutine { continuation ->
            repoRef.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<Z_AppCompt>()
                    snapshot.children.forEach { child ->
                        child.getValue(Z_AppCompt::class.java)?.let { item ->
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

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                factoryScope.launch {
                    try {
                        val localData = dao.getAll()
                        val localDataMap = localData.associateBy { it.keyID }
                        val firebaseKeyIds = mutableSetOf<String>()

                        for (child in snapshot.children) {
                            try {
                                child.getValue(Z_AppCompt::class.java)?.let { fbEntity ->
                                    val entityWithKey = fbEntity.copy(keyID = child.key ?: "")
                                    firebaseKeyIds.add(entityWithKey.keyID)

                                    val localEntity = localDataMap[entityWithKey.keyID]

                                    when {
                                        localEntity == null -> {
                                            dao.upsert(entityWithKey)
                                        }
                                        else -> {
                                            dao.deleteByKeyId(entityWithKey.keyID)
                                            dao.insert(entityWithKey)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }

                        val itemsToDelete = localDataMap.keys - firebaseKeyIds
                        for (keyToDelete in itemsToDelete) {
                            try {
                                dao.deleteByKeyId(keyToDelete)
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

    fun addOrUpdatedDataBase(
        existingIndex: Int,
        dataAvecTigerUpdate: Z_AppCompt
    ) {
        factoryScope.launch {
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
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }
}
