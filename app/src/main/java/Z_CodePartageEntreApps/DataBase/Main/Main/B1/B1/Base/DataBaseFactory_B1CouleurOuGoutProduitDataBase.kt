package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.WDatabaseInitializationManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class DataBaseFactory_B1CouleurOuGoutProduitDataBase(
    val dao: B1CouleurOuGoutProduitDataBaseDao,
) {

    val repoTAG = "B1CouleurOuGoutProduitDataBase"
    val repoRef = B1CouleurOuGoutProduitDataBase.ref
    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.4f)
        val data: List<B1CouleurOuGoutProduitDataBase> = if (isInternetAvailable) {
            updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.6f)
            onLoadFromFireBase()
        } else {
            emptyList()
        }
        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
    }

    suspend fun onLoadFromFireBase(): MutableList<B1CouleurOuGoutProduitDataBase> {
        return suspendCancellableCoroutine { continuation ->
            B1CouleurOuGoutProduitDataBase.ref.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<B1CouleurOuGoutProduitDataBase>()
                    snapshot.children.forEach { child ->
                        child.getValue(B1CouleurOuGoutProduitDataBase::class.java)?.let { item ->
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
                                child.getValue(B1CouleurOuGoutProduitDataBase::class.java)
                                    ?.let { entity ->
                                        val entityWithKey = entity.copy(id = child.key ?: "")
                                        val shouldUpdate = try {
                                            val localEntity =
                                                dao.getAll().find { it.id == entityWithKey.id }
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


    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
            }
        }
    }

    fun deleteDataAncienRepo(data: B1CouleurOuGoutProduitDataBase) {
        composScope.launch {
            dao.delete(data)
            deleteFromFireBase(data)
        }
    }

    private suspend fun deleteFromFireBase(data: B1CouleurOuGoutProduitDataBase) {
        repoRef.child(data.id).removeValue().await()
    }

    private suspend fun batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(datas: List<B1CouleurOuGoutProduitDataBase>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.id] = data
        }
        val firebaseRef = B1CouleurOuGoutProduitDataBase.ref
        firebaseRef.updateChildren(updates).await()
    }
}
