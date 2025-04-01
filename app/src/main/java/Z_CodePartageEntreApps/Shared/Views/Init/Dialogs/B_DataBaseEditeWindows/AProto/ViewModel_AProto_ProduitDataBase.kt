package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.AProto

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.AProto_ProduitDataBaseRepository
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
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
    private val AProto_ProduitDataBaseRepository: AProto_ProduitDataBaseRepository
) : ViewModel() {
    val TAG = "ViewModel_AProto_ProduitDataBase"

    // Progress tracking
    private val _migrationProgress = MutableStateFlow(0f)
    val migrationProgress = _migrationProgress.asStateFlow()

    // References to the old database structure
    private val sonAncienReference = ref_HeadOfModels.child("A_ProduitModel")

    /**
     * Retrieves data from the old database structure
     */
    @SuppressLint("RestrictedApi")
    private suspend fun getAncienDataBase(): List<A_ProduitModel> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d(TAG, "Starting retrieval from old database at path: ${sonAncienReference.path}")
                val snapshot = sonAncienReference.get().await()

                if (!snapshot.exists()) {
                    Log.e(TAG, "Error: Old database reference doesn't exist at path: ${sonAncienReference.path}")
                    return@withContext emptyList()
                }

                Log.d(TAG, "Successfully fetched data snapshot with ${snapshot.childrenCount} items")

                val result = snapshot.children.mapNotNull {
                    try {
                        val item = it.getValue(A_ProduitModel::class.java)
                        if (item == null) {
                            Log.w(TAG, "Warning: Failed to parse item with key: ${it.key}")
                        } else {
                            Log.v(TAG, "Successfully parsed item: ${item.id} - ${item.nom}")
                        }
                        item
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing item with key ${it.key}: ${e.message}", e)
                        null
                    }
                }

                Log.d(TAG, "Successfully parsed ${result.size}/${snapshot.childrenCount} items from old database")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving ancien produits: ${e.message}", e)
                Log.e(TAG, "Database path attempted: ${sonAncienReference.path}")
                Log.e(TAG, "Stack trace:", e)
                emptyList()
            }
        }

    /**
     * Migrates data from the old database structure to the new one
     */
    /**
     * Migrates data from the old database structure to the new one
     */
    fun populateModelearSonAncien() {
        Log.d(TAG, "Starting migration from old database structure to new one")
        viewModelScope.launch {
            try {
                _migrationProgress.value = 0.1f
                Log.d(TAG, "Migration progress: 10% - Starting data retrieval")

                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.d(TAG, "No ancien data found to migrate. Either database is empty or there was an error retrieving data")
                    _migrationProgress.value = 0f
                    return@launch
                }

                Log.d(TAG, "Migration progress: 30% - Retrieved ${ancienDataList.size} items from old database")
                _migrationProgress.value = 0.3f

                // Transform old data structure to new AProto_ProduitDataBase structure
                Log.d(TAG, "Starting data transformation from old to new structure")
                val newDataList = ancienDataList.mapIndexed { index, ancienData ->
                    try {
                        // Update progress during mapping (from 30% to 60%)
                        if (ancienDataList.size > 1) {
                            val progressIncrement = 0.3f / ancienDataList.size
                            _migrationProgress.value = 0.3f + (progressIncrement * index)
                        }

                        val newItem = AProto_ProduitDataBase(
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
                            porbableNonDispo = ancienData.etatesMutable.porbableNonDispo,
                            enumVarNonDispoPourClients = mapNonDispoPourClients(ancienData.etatesMutable.enumVarNonDispoPourClients),
                            parentCategoryId = ancienData.parentCategoryId,
                            indexInParentCategorie = ancienData.indexInParentCategorie,
                            monPrixAchat = ancienData.statuesBase.infosCoutes.monPrixAchat,
                            monPrixVent = ancienData.statuesBase.infosCoutes.monPrixVent
                        )
                        Log.v(TAG, "Transformed item ${index+1}/${ancienDataList.size}: ${newItem.id} - ${newItem.nom}")
                        newItem
                    } catch (e: Exception) {
                        Log.e(TAG, "Error transforming item ${ancienData.id}: ${e.message}", e)
                        // Return a default item to maintain consistency
                        AProto_ProduitDataBase(
                            id = ancienData.id,
                            nom = "Error: ${e.message}",
                            parentCategoryId = 0,
                            indexInParentCategorie = 0
                        )
                    }
                }

                Log.d(TAG, "Migration progress: 60% - Transformed ${newDataList.size} items to new structure")
                _migrationProgress.value = 0.6f

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<AProto_ProduitDataBase>()
                snapshotList.addAll(newDataList)
                Log.d(TAG, "Created SnapshotStateList with ${snapshotList.size} items")

                // Update repository with new data structure
                Log.d(TAG, "Starting repository update with transformed data")
                try {
                    // Update progress to 80% before database write
                    _migrationProgress.value = 0.8f
                    AProto_ProduitDataBaseRepository.updateMultiDatas(snapshotList)
                    Log.d(TAG, "Repository update successful")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating repository: ${e.message}", e)
                    throw e
                }

                _migrationProgress.value = 1.0f
                Log.d(TAG, "Migration progress: 100% - Migration complete")
                Log.d(TAG, "Successfully migrated ${newDataList.size} category products from ancien database")
            } catch (e: Exception) {
                Log.e(TAG, "Error migrating ancien database: ${e.message}", e)
                Log.e(TAG, "Stack trace:", e)
                _migrationProgress.value = 0f
            }
        }
    }

    /**
     * Maps the old enum to the new enum type
     */
    private fun mapNonDispoPourClients(oldEnum: A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS): AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS {
        return when (oldEnum) {
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.TOUT -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.TOUT
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.NEVEAU -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.NEVEAU
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.DEFINIE -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.DEFINIE
        }
    }

    /**
     * Updates a single product category
     */
    fun updateData(categorieProduit: AProto_ProduitDataBase) {
        Log.d(TAG, "Updating single product category: ${categorieProduit.id} - ${categorieProduit.nom}")
        AProto_ProduitDataBaseRepository.updateUnSeulData(categorieProduit)
    }

    /**
     * Updates multiple product categories at once
     */
    suspend fun updateMultiDatas(data: SnapshotStateList<AProto_ProduitDataBase>) {
        Log.d(TAG, "Updating multiple product categories: ${data.size} items")
        AProto_ProduitDataBaseRepository.updateMultiDatas(data)
    }
}
