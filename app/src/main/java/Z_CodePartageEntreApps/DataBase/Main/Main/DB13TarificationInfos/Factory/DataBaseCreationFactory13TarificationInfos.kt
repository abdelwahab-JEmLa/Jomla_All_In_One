package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseCreationFactory13TarificationInfos(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.dao_M13TarificationInfos()
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    val repoRef = M13TarificationInfos.ref
    val repoEntityName = "DataBaseCreationFactoryM13TarificationInfos"
    val repoTAG = repoEntityName
    val name = Repository.M13TarificationInfosEntity.name
    var isListenerRegistered = false
    private var isPendingSync = false
    private var retryCount = 0
    private val maxRetries = 3
    private val retryDelayMs = 5000L // 5 seconds

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(name, 0.4f)

        val data: List<M13TarificationInfos> = if (isInternetAvailable) {
            updateRepoProgress(name, 0.6f)
            fetchDataFromFirebase()
        } else {
            isPendingSync = true
            updateRepoProgress(name, 0.6f)
            loadDefaultOrCachedData()
        }

        updateRepoProgress(name, 0.8f)

        if (data.isNotEmpty()) {
            dao.insertAll(data)
        }

        updateRepoProgress(name, 1.0f)
    }

    suspend fun fetchDataFromFirebase(): List<M13TarificationInfos> {
        return suspendCancellableCoroutine { continuation ->
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
                .addOnFailureListener { exception ->
                    continuation.resume(emptyList()) // Return empty list on failure
                }
        }
    }

    private suspend fun loadDefaultOrCachedData(): List<M13TarificationInfos> {
        return emptyList()

    }

    suspend fun retryInitWhenConnectionReturns(
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!isPendingSync) return

        retryCount++

        try {
            updateRepoProgress(name, 0.2f)

            val data = fetchDataFromFirebase()

            if (data.isNotEmpty()) {
                updateRepoProgress(name, 0.8f)

                dao.deleteAll()
                dao.insertAll(data)

                isPendingSync = false
                retryCount = 0

                updateRepoProgress(name, 1.0f)
            } else {
                // If still no data and we haven't exceeded max retries, schedule another retry
                if (retryCount < maxRetries) {
                    factoryScope.launch {
                        delay(retryDelayMs)
                        retryInitWhenConnectionReturns(updateRepoProgress)
                    }
                } else {
                    // Max retries reached, stop trying
                    isPendingSync = false
                    retryCount = 0
                }
            }
        } catch (e: Exception) {
            // Handle retry failure
            if (retryCount < maxRetries) {
                factoryScope.launch {
                    delay(retryDelayMs)
                    retryInitWhenConnectionReturns(updateRepoProgress)
                }
            } else {
                isPendingSync = false
                retryCount = 0
            }
        }
    }

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
                                // Log error if needed
                            }
                        }

                        // If we had pending sync and listener is working, mark sync as complete
                        if (isPendingSync && updateCount > 0) {
                            isPendingSync = false
                            retryCount = 0
                        }
                    } catch (e: Exception) {
                        // Log error if needed
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
            }
        })}
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

    // Call this method when internet connection is restored
    fun onInternetConnectionRestored(updateRepoProgress: (String, Float) -> Unit) {
        if (isPendingSync) {
            factoryScope.launch {
                retryInitWhenConnectionReturns(updateRepoProgress)
            }
        }
    }

    // Helper method to check if sync is pending
    fun hasPendingSync(): Boolean = isPendingSync
}
