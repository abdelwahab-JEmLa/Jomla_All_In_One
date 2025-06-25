package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A1.Proto.Juin17.Proto.WDatabaseInitializationManager
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.io.File
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
            updateRepoProgress(
                WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name,
                0.6f
            )
            onLoadFromFireBase()
        } else {
            emptyList()
        }
        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
    }

    fun initCreationDepuitOld(
        produit: ArticlesBasesStatsTable,
    ): List<B1CouleurOuGoutProduitDataBase> {

        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
        val results = mutableListOf<B1CouleurOuGoutProduitDataBase>()

        val colorMappings = listOf(
            produit.couleur1 to 0,
            produit.couleur2 to 1,
            produit.couleur3 to 2,
            produit.couleur4 to 3
        )

        colorMappings.forEach { (couleur, colorIndex) ->
            val imageIndex = colorIndex + 1
            val fileName = "${produit.id}_$imageIndex"

            val imageFile = listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
                ?: File("$basePath/NonTrouve.webp")

            val imageExists = imageFile.name != "NonTrouve.webp" &&
                    imageFile.exists() && imageFile.canRead() && imageFile.length() > 0

            if (imageExists || !couleur.isNullOrBlank()) {
                /*
                ProtoKey
                val couleurNom = buildString {

                    if (imageExists) {
                        append(imageFile.nameWithoutExtension)
                        append("_")
                        append(imageFile.extension)
                    } else {
                        append(produit.id.toString())
                        append("ProtoImgParIndex_")
                        append(colorIndex)
                    }
                }

                val key = buildString {
                    append("B0")
                    append("-")
                    append(produit.id.toString())
                    append("--")
                    append("B1")
                    append("-")
                    append(couleurNom)
                }               */
                val colorData = B1CouleurOuGoutProduitDataBase(
                    aAffiche = if (imageExists) B1CouleurOuGoutProduitDataBase.Type.Image else B1CouleurOuGoutProduitDataBase.Type.Nom,
                    nomImageFichie = imageFile.name,
                    nomCouleurStrSiSonImageDispo = couleur ?: "",
                    parentBProduitOldID = produit.id,
                    parentBProduitNom = produit.nom
                )

                results.add(colorData)
            }
        }

        return results
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
                .addOnFailureListener { exception ->
                    println("Firebase load error: ${exception.message}")
                    continuation.resume(mutableListOf()) // Return empty list instead of throwing
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
                                        val entityWithKey = entity.copy(key = child.key ?: "")
                                        val shouldUpdate = try {
                                            val localEntity =
                                                dao.getAll().find { it.key == entityWithKey.key }
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
                                println("Error processing child: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Error in data change listener: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase listener cancelled: ${error.message}")
                isListenerRegistered = false
            }
        })
    }

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            try {
                if (existingIndex >= 0) {
                    dao.update(dataAvecTigerUpdate)
                    batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
                } else {
                    dao.insert(dataAvecTigerUpdate)
                    batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
                }
            } catch (e: Exception) {
                println("Error in addOrUpdatedAncienRepo: ${e.message}")
            }
        }
    }

    fun deleteDataAncienRepo(data: B1CouleurOuGoutProduitDataBase) {
        composScope.launch {
            try {
                dao.delete(data)
                deleteFromFireBase(data)
            } catch (e: Exception) {
                println("Error in deleteDataAncienRepo: ${e.message}")
            }
        }
    }

    private suspend fun deleteFromFireBase(data: B1CouleurOuGoutProduitDataBase) {
        try {
            repoRef.child(data.key).removeValue().await()
        } catch (e: Exception) {
            println("Error deleting from Firebase: ${e.message}")
        }
    }

    private suspend fun batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(datas: List<B1CouleurOuGoutProduitDataBase>) {
        try {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.key] = data
            }
            val firebaseRef = B1CouleurOuGoutProduitDataBase.ref
            firebaseRef.updateChildren(updates).await()
        } catch (e: Exception) {
            println("Error in batch Firebase update: ${e.message}")
        }
    }

    fun deleteAll() {
        composScope.launch {
            try {
                // Delete from local database
                dao.deleteAll()

                // Delete from Firebase
                repoRef.removeValue().await()
            } catch (e: Exception) {
                // Handle error - could log or throw based on requirements
                println("Error deleting all data: ${e.message}")
            }
        }
    }
}
