package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseInitFactory_19Etudiant(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.Dao19Etudiant()
    private val factoryScope = CoroutineScope(Dispatchers.IO)
    val repoRef = M19Etudiant.ref
    val name = Repository.Entity_19Etudiant.name
    var isListenerRegistered = false

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
            val data: List<M19Etudiant> = if (isInternetAvailable) {
                updateRepoProgress(name, 0.6f)
                onLoadFromFireBase()
            } else {
                emptyList()
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

    suspend fun onLoadFromFireBase(): MutableList<M19Etudiant> {
        return suspendCancellableCoroutine { continuation ->
            repoRef.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M19Etudiant>()
                    snapshot.children.forEach { child ->
                        child.getValue(M19Etudiant::class.java)?.let { item ->
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
        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

            repoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    factoryScope.launch {
                        try {
                            val localData = dao.getAll()
                            val localDataMap = localData.associateBy { it.keyID }
                            val firebaseKeyIds = mutableSetOf<String>()

                            for (child in snapshot.children) {
                                try {
                                    child.getValue(M19Etudiant::class.java)?.let { fbEntity ->
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
            })}
    }

    fun set(dataAvecTigerUpdate: M19Etudiant) {
        factoryScope.launch {
            val entityWithUpdatedTimestamp = dataAvecTigerUpdate.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            dao.upsert(entityWithUpdatedTimestamp)
            batchFireBaseUpdateGBonVent(listOf(entityWithUpdatedTimestamp))
        }
    }

    // SOLUTION 1: Utiliser setValue au lieu de updateChildren
    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M19Etudiant>) {
        try {
            // Pour chaque donnée, faire un setValue individuel
            datas.forEach { data ->
                repoRef.child(data.keyID).setValue(data).await()
            }
        } catch (e: Exception) {
            // Log l'erreur
            e.printStackTrace()
        }
    }


    fun delete(data: M19Etudiant) {
        factoryScope.launch {
            try {
                dao.delete(data)
                repoRef.child(data.keyID).removeValue().await()
            } catch (e: Exception) {
            }
        }
    }
}
