package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.A_Produit

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.Z.Archive.A_ProduitAncienModelStructure
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModel_AProto_ProduitDataBase(
    private val a_ProduitRepository: A_ProduitRepository,
    _0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys,
) : ViewModel() {
    val TAG = "ViewModel_AProto_ProduitDataBase"
    val _2_1_ProduitsDataBase_Repository =_0_0_HeadSQLRepositorys.repositorys_Model._2_1_ProduitsDataBase_Repository
    // Progress tracking
    private val _migrationProgress = MutableStateFlow(0f)
    val migrationProgress = _migrationProgress.asStateFlow()

    // References to the old database structure
    private val sonAncienReference = ref_HeadOfModels.child("A_ProduitModel")

    /**
     * Retrieves data from the old database structure
     */
    @SuppressLint("RestrictedApi")
    private suspend fun getAncienDataBase(): List<A_ProduitAncienModelStructure> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d(TAG, "Retrieving data from old database structure...")
                Log.d(TAG, "Database reference path: ${sonAncienReference.path}")
                Log.d(TAG, "Database reference complete URL: ${sonAncienReference.toString()}")

                // Log parent reference details
                Log.d(TAG, "Parent reference path: ${ref_HeadOfModels.path}")
                Log.d(TAG, "Parent reference URL: ${ref_HeadOfModels.toString()}")

                val snapshot = sonAncienReference.get().await()

                Log.d(TAG, "Snapshot exists: ${snapshot.exists()}")
                Log.d(TAG, "Snapshot has children: ${snapshot.hasChildren()}")
                if (snapshot.exists()) {
                    Log.d(TAG, "Number of children: ${snapshot.childrenCount}")
                    // Log first few keys if available
                    snapshot.children.take(5).forEach { child ->
                        Log.d(TAG, "Child key: ${child.key}, has data: ${child.value != null}")
                    }
                }

                if (!snapshot.exists()) {
                    Log.w(TAG, "Old database structure doesn't exist or is empty")
                    return@withContext emptyList()
                }

                val result = snapshot.children.mapNotNull {
                    try {
                        val value = it.getValue(A_ProduitAncienModelStructure::class.java)
                        Log.d(TAG, "Successfully parsed item with key: ${it.key}, name: ${value?.nom}")
                        value
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing item with key: ${it.key}, error: ${e.message}")
                        null
                    }
                }

                Log.d(TAG, "Successfully retrieved ${result.size} items from old database")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving data from old database: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                emptyList()
            }
        }


    /**
     * Migrates data from the old database structure to the new one
     */
    fun populateModelearSonAncien() {
        Log.d(TAG, "Starting migration process from old to new database structure")
        viewModelScope.launch {
            try {
                _migrationProgress.value = 0.1f
                Log.d(TAG, "Migration progress: 10% - Preparing to fetch old data")

                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.w(TAG, "Migration aborted: No data found in old database structure")
                    _migrationProgress.value = 0f
                    return@launch
                }

                Log.d(TAG, "Retrieved ${ancienDataList.size} items from old database structure")
                _migrationProgress.value = 0.3f
                Log.d(TAG, "Migration progress: 30% - Beginning data transformation")

                // Transform old data structure to new A_Produit structure
                val newDataList = mutableListOf<A_Produit>()
                ancienDataList.forEachIndexed { index, ancienData ->
                    try {
                        // Update progress during mapping (from 30% to 60%)
                        if (ancienDataList.size > 1) {
                            val progressIncrement = 0.3f / ancienDataList.size
                            _migrationProgress.value = 0.3f + (progressIncrement * index)
                        }

                        Log.d(TAG, "Transforming item ${index+1}/${ancienDataList.size}: ID=${ancienData.id}, Name=${ancienData.nom}")

                        val newItem = A_Produit(
                            id = ancienData.id,
                            nom = ancienData.nom,
                            emballageCartone = ancienData.statuesBase.characterProduit.emballageCartone,
                            itsTempProduit = ancienData.itsTempProduit,
                            besoinToBeUpdated = ancienData.besoinToBeUpdated,
                            non_Trouve = ancienData.non_Trouve,
                            isVisible = ancienData.isVisible,
                            imageGlidReloadTigger = ancienData.statuesBase.imageGlidReloadTigger,
                            prePourCameraCapture = ancienData.statuesBase.prePourCameraCapture,
                            diponibilityEtate = ancienData.etatesMutable.diponibilityEtate,
                            probablementNonDispo = ancienData.etatesMutable.porbableNonDispo,
                            enumVarNonDispoPourClients = getDepuitenumVarNonDispoPourClients(ancienData.etatesMutable.nonDispoPourClientsString),
                            parentCategoryId = ancienData.parentCategoryId,
                            indexInParentCategorie = ancienData.indexInParentCategorie,
                            monPrixAchat = ancienData.statuesBase.infosCoutes.monPrixAchat,
                            monPrixVent = ancienData.statuesBase.infosCoutes.monPrixVent
                        )

                        Log.d(TAG, "Successfully transformed item ${index+1}: ${newItem.nom}")
                        newDataList.add(newItem)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error transforming item ${index+1}: ${e.message}")
                        // Return a default item to maintain consistency
                        val defaultItem = A_Produit(
                            id = ancienData.id,
                            nom = "Error: ${e.message}",
                            parentCategoryId = 0,
                            indexInParentCategorie = 0
                        )
                        Log.d(TAG, "Using default item for ${index+1} due to error")
                        newDataList.add(defaultItem)
                    }
                }

                Log.d(TAG, "Transformation complete. Transformed ${newDataList.size} items")
                _migrationProgress.value = 0.6f
                Log.d(TAG, "Migration progress: 60% - Creating SnapshotStateList")

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<A_Produit>()
                snapshotList.addAll(newDataList)
                Log.d(TAG, "Created SnapshotStateList with ${snapshotList.size} items")

                // Convert A_Produit to _2_1_ProduitsDataBase objects
                Log.d(TAG, "Converting A_Produit objects to _2_1_ProduitsDataBase objects")
                val convertedData = snapshotList.mapIndexed { index, produit ->
                    Log.d(TAG, "Converting item ${index+1}/${snapshotList.size}: ${produit.nom}")
                    convertAProduitToProduitsDataBase(produit)
                }

                Log.d(TAG, "Conversion complete. Ready to upsertEtReturnSonNewVid ${convertedData.size} items into _2_1_ProduitsDataBase")
                // Update progress to 80% before database write
                _migrationProgress.value = 0.8f
                Log.d(TAG, "Migration progress: 80% - Starting database insertion")

                // Check database repository before insertion
                Log.d(TAG, "Repository instance check: ${_2_1_ProduitsDataBase_Repository != null}")

                // Use the repository to add multiple items at once and get their VIDs
                Log.d(TAG, "Calling addMultiDATAsEtReturnVIDsList with ${convertedData.size} items")
                _2_1_ProduitsDataBase_Repository.addMultiDATAsEtReturnVIDsList(convertedData) { vids ->
                    // Log the VIDs being returned for tracking purposes
                    Log.d(TAG, "Received ${vids.size} VIDs from database insertion")

                    if (vids.isEmpty()) {
                        Log.e(TAG, "No VIDs returned from database - insertion may have failed")
                    } else if (vids.size != convertedData.size) {
                        Log.w(TAG, "Mismatch in numbers: Expected ${convertedData.size} VIDs but got ${vids.size}")
                    }

                    for (i in vids.indices) {
                        Log.d(TAG, "VID[$i]: ${vids[i]} for product: ${if (i < convertedData.size) convertedData[i].nom else "unknown"}")
                    }

                    // Update the original A_Produit objects with new VIDs if necessary
                    Log.d(TAG, "Updating A_Produit objects with new VIDs")
                    vids.forEachIndexed { index, vid ->
                        if (index < snapshotList.size) {
                            snapshotList[index].id = vid
                            Log.d(TAG, "Updated A_Produit[${index}] with VID: $vid, name: ${snapshotList[index].nom}")
                        } else {
                            Log.w(TAG, "Cannot upsert_1_3_TransactionCommercial A_Produit with index $index - index out of bounds")
                        }
                    }

                    // Update A_ProduitRepository with the updated objects
                    viewModelScope.launch {
                        try {
                            Log.d(TAG, "Starting upsert_1_3_TransactionCommercial of ${snapshotList.size} products in A_ProduitRepository")
                            a_ProduitRepository.updateMultiDatas(snapshotList)
                            Log.d(TAG, "Successfully updated ${snapshotList.size} products in A_ProduitRepository")
                            _migrationProgress.value = 1.0f
                            Log.d(TAG, "Migration progress: 100% - Migration complete")
                        } catch (e: Exception) {
                            _migrationProgress.value = 0f
                            Log.e(TAG, "Error updating A_ProduitRepository: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                _migrationProgress.value = 0f
                Log.e(TAG, "Error in populateModelearSonAncien: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun convertAProduitToProduitsDataBase(produit: A_Produit): _2_1_ProduitsDataBase {
        Log.d(TAG, "Converting A_Produit to _2_1_ProduitsDataBase: ID=${produit.id}, Name=${produit.nom}")
        return _2_1_ProduitsDataBase(
            vid = produit.id,
            nom = produit.nom,
            emballageCartone = produit.emballageCartone,
            itsTempProduit = produit.itsTempProduit,
            besoinToBeUpdated = produit.besoinToBeUpdated,
            non_Trouve = produit.non_Trouve,
            isVisible = produit.isVisible,
            imageGlidReloadTigger = produit.imageGlidReloadTigger,
            prePourCameraCapture = produit.prePourCameraCapture,
            diponibilityEtate = produit.diponibilityEtate,
            probablementNonDispo = produit.probablementNonDispo,
            enumVarNonDispoPourClients = convertNonDispoPourClients(produit.enumVarNonDispoPourClients),
            parentCategoryId = produit.parentCategoryId,
            indexInParentCategorie = produit.indexInParentCategorie,
            monPrixAchat = produit.monPrixAchat,
            monPrixVent = produit.monPrixVent
        )
    }

    private fun convertNonDispoPourClients(source: A_Produit.NON_DISPO_POUR_CLIENTS): _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS {
        Log.d(TAG, "Converting NON_DISPO_POUR_CLIENTS enum: $source")
        return when (source) {
            A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            A_Produit.NON_DISPO_POUR_CLIENTS.TOUT -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.TOUT
            A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.NEVEAU
            A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.DEFINIE
        }
    }

    private fun getDepuitenumVarNonDispoPourClients(nonDispoPourClientsString: String?): A_Produit.NON_DISPO_POUR_CLIENTS {
        Log.d(TAG, "Converting string to NON_DISPO_POUR_CLIENTS enum: $nonDispoPourClientsString")
        return when (nonDispoPourClientsString) {
            "DISPONIBLE_POUR_TOUT" -> A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            "TOUT" -> A_Produit.NON_DISPO_POUR_CLIENTS.TOUT
            "NEVEAU" -> A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU
            "DEFINIE" -> A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE
            else -> {
                Log.w(TAG, "Unknown NON_DISPO_POUR_CLIENTS value: $nonDispoPourClientsString, using default DISPONIBLE_POUR_TOUT")
                A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT // Default value
            }
        }
    }

    fun updateData(categorieProduit: A_Produit) {
        Log.d(TAG, "Updating single product: ${categorieProduit.nom}")
        a_ProduitRepository.updateUnSeulData(categorieProduit)
    }

    suspend fun updateMultiDatas(data: SnapshotStateList<A_Produit>) {
        Log.d(TAG, "Updating multiple products: ${data.size}")
        a_ProduitRepository.updateMultiDatas(data)
    }
}
