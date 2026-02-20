package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Modules.AppDatabase
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager.Repository
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

class DataBaseInitFactory_8BonVent(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.GBonVentDao()
    private val factoryScope = CoroutineScope(Dispatchers.IO)
    val repoRef = M8BonVent.ref
    val name = Repository.Entity_8BonVent.name
    var isListenerRegistered = false

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {         //<--
        Log.d("suit_flow", "🔧 Factory8.init: Début de l'initialisation")

        val isTableEmpty = dao.isTableEmpty()
        Log.d("suit_flow", "📊 Factory8.init: Table vide? $isTableEmpty")

        val datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale = if (isInternetAvailable && !isTableEmpty) {
            try {
                updateRepoProgress(name, 0.2f)
                Log.d("suit_flow", "📥 Factory8.init: Chargement des données locales")

                val localData = dao.getAll()
                val localDataMap = localData.associateBy { it.keyID }
                Log.d("suit_flow", "✅ Factory8.init: ${localData.size} données locales chargées")

                updateRepoProgress(name, 0.4f)
                Log.d("suit_flow", "☁️ Factory8.init: Chargement depuis Firebase")

                val firebaseData = onLoadFromFireBase()
                Log.d("suit_flow", "✅ Factory8.init: ${firebaseData.size} données Firebase chargées")

                updateRepoProgress(name, 0.6f)

                val filtered = firebaseData.filter { fireBase_Data ->
                    val local_Data = localDataMap[fireBase_Data.keyID]
                    local_Data == null ||
                            fireBase_Data.dernierTimeTampsSynchronisationAvecFireBase >= local_Data.dernierTimeTampsSynchronisationAvecFireBase
                }

                Log.d("suit_flow", "🔄 Factory8.init: ${filtered.size} données à synchroniser")
                filtered
            } catch (e: Exception) {
                Log.e("suit_flow", "❌ Factory8.init: Erreur lors de la synchronisation - ${e.message}", e)
                emptyList()
            }
        } else {
            emptyList()
        }

        if (isTableEmpty) {
            updateRepoProgress(name, 0.4f)
            Log.d("suit_flow", "📥 Factory8.init: Table vide - Chargement initial")

            val data: List<M8BonVent> = if (isInternetAvailable) {
                updateRepoProgress(name, 0.6f)
                Log.d("suit_flow", "☁️ Factory8.init: Chargement depuis Firebase")
                onLoadFromFireBase()
            } else {
                Log.d("suit_flow", "📄 Factory8.init: Chargement depuis CSV (mode offline)")
                onLoadCategoriesFromCsv()
            }

            updateRepoProgress(name, 0.8f)
            Log.d("suit_flow", "💾 Factory8.init: Insertion de ${data.size} éléments dans Room")
            dao.insertAll(data)
            Log.d("suit_flow", "✅ Factory8.init: Insertion terminée")

        } else if (datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale.isNotEmpty()) {
            updateRepoProgress(name, 0.8f)
            Log.d("suit_flow", "🔄 Factory8.init: Upsert de ${datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale.size} éléments")

            datas_ac_Deffirent_Time_or_Non_Dispo_Au_Locale.forEach { item ->
                dao.upsert(item)
            }
            Log.d("suit_flow", "✅ Factory8.init: Upsert terminé")
        }

        updateRepoProgress(name, 1.0f)
        Log.d("suit_flow", "✅ Factory8.init: Initialisation terminée avec succès")
    }

    suspend fun onLoadFromFireBase(): MutableList<M8BonVent> {
        Log.d("suit_flow", "☁️ Factory8.onLoadFromFireBase: Début du chargement")

        return suspendCancellableCoroutine { continuation ->
            repoRef.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M8BonVent>()
                    snapshot.children.forEach { child ->
                        child.getValue(M8BonVent::class.java)?.let { item ->
                            dataList.add(item)
                        }
                    }
                    Log.d("suit_flow", "✅ Factory8.onLoadFromFireBase: ${dataList.size} éléments chargés")
                    continuation.resume(dataList)
                }
                .addOnFailureListener { e ->
                    Log.e("suit_flow", "❌ Factory8.onLoadFromFireBase: Erreur - ${e.message}", e)
                    throw IllegalStateException("No data available from Firebase or CSV")
                }
        }
    }

    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) {
            Log.d("suit_flow", "⚠️ Factory8.triggerListener: Listener déjà enregistré")
            return
        }

        isListenerRegistered = true
        Log.d("suit_flow", "🎧 Factory8.triggerListener: Enregistrement du listener Firebase")

        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {
            repoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("suit_flow", "🔔 Factory8.Listener: Changement Firebase détecté")

                    factoryScope.launch {
                        try {
                            val localData = dao.getAll()
                            val localDataMap = localData.associateBy { it.keyID }
                            val firebaseKeyIds = mutableSetOf<String>()

                            Log.d("suit_flow", "📊 Factory8.Listener: ${localData.size} données locales, ${snapshot.childrenCount} dans Firebase")

                            for (child in snapshot.children) {
                                try {
                                    child.getValue(M8BonVent::class.java)?.let { fbEntity ->
                                        val entityWithKey = fbEntity.copy(keyID = child.key ?: "")
                                        firebaseKeyIds.add(entityWithKey.keyID)

                                        val localEntity = localDataMap[entityWithKey.keyID]

                                        when {
                                            localEntity == null -> {
                                                Log.d("suit_flow", "➕ Factory8.Listener: Nouvelle entité - ${entityWithKey.keyID.takeLast(4)}")
                                                dao.upsert(entityWithKey)
                                            }
                                            else -> {
                                                Log.d("suit_flow", "🔄 Factory8.Listener: Mise à jour entité - ${entityWithKey.keyID.takeLast(4)}")
                                                dao.deleteByKeyId(entityWithKey.keyID)
                                                dao.insert(entityWithKey)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("suit_flow", "❌ Factory8.Listener: Erreur traitement enfant - ${e.message}")
                                }
                            }

                            val itemsToDelete = localDataMap.keys - firebaseKeyIds
                            if (itemsToDelete.isNotEmpty()) {
                                Log.d("suit_flow", "🗑️ Factory8.Listener: ${itemsToDelete.size} éléments à supprimer")
                                for (keyToDelete in itemsToDelete) {
                                    try {
                                        dao.deleteByKeyId(keyToDelete)
                                    } catch (e: Exception) {
                                        Log.e("suit_flow", "❌ Factory8.Listener: Erreur suppression - ${e.message}")
                                    }
                                }
                            }

                            Log.d("suit_flow", "✅ Factory8.Listener: Synchronisation terminée")
                        } catch (e: Exception) {
                            Log.e("suit_flow", "❌ Factory8.Listener: Erreur globale - ${e.message}", e)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("suit_flow", "❌ Factory8.Listener: Listener annulé - ${error.message}")
                    isListenerRegistered = false
                }
            })
        }
    }

    fun set(dataAvecTigerUpdate: M8BonVent) {
        Log.d("suit_flow", "💾 Factory8.set: Début - keyID=${dataAvecTigerUpdate.keyID.takeLast(4)}")
        Log.d("suit_flow", "📝 Factory8.set: isPrinted=${dataAvecTigerUpdate.a_etai_imprime_au_moi_ne_foit}")

        factoryScope.launch {
            val entityWithUpdatedTimestamp = dataAvecTigerUpdate.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )

            Log.d("suit_flow", "💾 Factory8.set: Upsert dans Room DAO")
            dao.upsert(entityWithUpdatedTimestamp)
            Log.d("suit_flow", "✅ Factory8.set: Upsert Room terminé - Le Flow va se déclencher")

            Log.d("suit_flow", "☁️ Factory8.set: Mise à jour Firebase")
            batchFireBaseUpdateGBonVent(listOf(entityWithUpdatedTimestamp))
            Log.d("suit_flow", "✅ Factory8.set: Mise à jour Firebase terminée")
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M8BonVent>) {
        Log.d("suit_flow", "☁️ Factory8.batchUpdate: Début - ${datas.size} éléments")

        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }

        repoRef.updateChildren(updates).await()
        Log.d("suit_flow", "✅ Factory8.batchUpdate: Mise à jour Firebase réussie")
    }

    fun delete(data: M8BonVent) {
        Log.d("suit_flow", "🗑️ Factory8.delete: Début - keyID=${data.keyID.takeLast(4)}")

        factoryScope.launch {
            try {
                dao.delete(data)
                Log.d("suit_flow", "✅ Factory8.delete: Supprimé de Room")

                repoRef.child(data.keyID).removeValue().await()
                Log.d("suit_flow", "✅ Factory8.delete: Supprimé de Firebase")
            } catch (e: Exception) {
                Log.e("suit_flow", "❌ Factory8.delete: Erreur - ${e.message}", e)
            }
        }
    }
}
