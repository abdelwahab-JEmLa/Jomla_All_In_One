package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager.Repository
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.D_AchatOperation
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init.onLoadFromFireBaseD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init.onLoadCategoriesFromCsvD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.D_AchatOperationDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class D_AchatOperationDataBaseProtoJuin17(
    val dao: D_AchatOperationDao,
) {
    val repoTAG = "D_AchatOperation"
    val repoRef = D_AchatOperation.caRef
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val itsTestModel = true

    fun getTestDate(): List<D_AchatOperation> {
        // Create test hard data for development/testing purposes
        return listOf(
            D_AchatOperation(
                bsonObjectId = "test_achat_001",
                creationTimesTamp = System.currentTimeMillis(),
                nomImageFichieOuApellationDuCouleur = "Produit Test 1",
                parentBonVentObjectId = "bon_001",
                parentProduitBsonObjectId = "produit_001",
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 1L,
                quantityAchete = 5,
                provisoireMonPrix = 150.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.Affiche
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_002",
                creationTimesTamp = System.currentTimeMillis(),
                nomImageFichieOuApellationDuCouleur = "Produit Test 2",
                parentBonVentObjectId = "bon_001",
                parentProduitBsonObjectId = "produit_002",
                parentComptVendeurCreateurObjectId = "vendeur_001",
                clientParentObjectId = "client_001",
                produitAcheterAncienID = 2L,
                quantityAchete = 3,
                provisoireMonPrix = 200.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_003",
                creationTimesTamp = System.currentTimeMillis(),
                nomImageFichieOuApellationDuCouleur = "Produit Test 3",
                parentBonVentObjectId = "bon_002",
                parentProduitBsonObjectId = "produit_003",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                clientParentObjectId = "client_002",
                produitAcheterAncienID = 3L,
                quantityAchete = 2,
                provisoireMonPrix = 75.5,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.Affiche
            ),
            D_AchatOperation(
                bsonObjectId = "test_achat_004",
                creationTimesTamp = System.currentTimeMillis(),
                nomImageFichieOuApellationDuCouleur = "Produit Test 4",
                parentBonVentObjectId = "bon_002",
                parentProduitBsonObjectId = "produit_004",
                parentComptVendeurCreateurObjectId = "vendeur_002",
                clientParentObjectId = "client_002",
                produitAcheterAncienID = 4L,
                quantityAchete = 1,
                provisoireMonPrix = 500.0,
                etateActuellementEst = D_AchatOperation.EtateActuellementEst.CONFIRME
            )
        )
    }

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.4f)

        val data: List<D_AchatOperation> = if (itsTestModel) {
            dao.deleteAll()
            getTestDate()
        } else if (isInternetAvailable) {
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
                                child.getValue(D_AchatOperation::class.java)?.let { entity ->
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

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: D_AchatOperation
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

    private suspend fun batchFireBaseUpdateD_AchatOperation(datas: List<D_AchatOperation>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.bsonObjectId] = data
        }
        val firebaseRef = D_AchatOperation.caRef
        firebaseRef.updateChildren(updates).await()
    }
}
